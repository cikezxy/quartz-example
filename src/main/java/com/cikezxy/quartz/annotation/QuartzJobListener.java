package com.cikezxy.quartz.annotation;

import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.JobDetailImpl;
import org.quartz.impl.triggers.CronTriggerImpl;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.support.ArgumentConvertingMethodInvoker;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

@Component
public class QuartzJobListener implements ApplicationListener<ContextRefreshedEvent>, BeanPostProcessor {
    @Autowired
    private Scheduler scheduler;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private QuartzJobRepository repo;
    private Map<JobDetail, CronTrigger> jobTriggerMap = new HashMap<JobDetail, CronTrigger>();


    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        try {
            if (this.applicationContext != event.getApplicationContext()) {
                return;
            }
            this.scheduleJobs(jobTriggerMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void processQuartzJob(Method method, Object bean) throws ParseException, NoSuchMethodException, ClassNotFoundException {

        QuartzJob quartzJobAnnotation = AnnotationUtils.getAnnotation(method, QuartzJob.class);
        if (quartzJobAnnotation == null) {
            return;
        }

        ArgumentConvertingMethodInvoker invoker = new ArgumentConvertingMethodInvoker();
        invoker.setTargetObject(bean);
        invoker.setTargetMethod(method.getName());
        invoker.prepare();

        CronTriggerImpl cronTriggerBean = new CronTriggerImpl();
        cronTriggerBean.setCronExpression(quartzJobAnnotation.cron());
        cronTriggerBean.setName(method.getName() + "_trigger");
        cronTriggerBean.setJobGroup(quartzJobAnnotation.group());
        cronTriggerBean.setStartTime(new Date(System.currentTimeMillis() + quartzJobAnnotation.startDelayMills()));
        cronTriggerBean.setTimeZone(TimeZone.getDefault());

        JobDetailImpl jobDetail = new JobDetailImpl();
        jobDetail.setName(method.getName());
        jobDetail.setGroup(quartzJobAnnotation.group());
        jobDetail.setJobClass(MethodInvokingJob.class);

        jobTriggerMap.put(jobDetail, cronTriggerBean);
        repo.putJob(jobDetail, invoker);
    }


    protected void scheduleJobs(Map<JobDetail, CronTrigger> jobDetailCronTriggerMap) {
        for (Map.Entry<JobDetail, CronTrigger> entry : jobDetailCronTriggerMap.entrySet()) {
            try {
                scheduler.scheduleJob(entry.getKey(), entry.getValue());
            } catch (SchedulerException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        final Class<?> targetClass = AopUtils.getTargetClass(bean);
        final Object b = bean;
        try {
            ReflectionUtils.doWithMethods(targetClass, new ReflectionUtils.MethodCallback() {

                @Override
                public void doWith(Method method) throws IllegalArgumentException, IllegalAccessException {
                    try {
                        processQuartzJob(method, b);
                    } catch (ParseException e) {
                        throw new IllegalArgumentException(e);
                    } catch (ClassNotFoundException e1) {
                        throw new IllegalArgumentException(e1);
                    } catch (NoSuchMethodException e2) {
                        throw new IllegalArgumentException(e2);
                    }
                }
            });
        } catch (Exception e) {
            throw new BeanCreationException("Fail to create bean", e);
        }
        return bean;
    }
}
