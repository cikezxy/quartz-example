package com.cikezxy.quartz.annotation;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.*;
import org.quartz.impl.JobDetailImpl;
import org.quartz.impl.triggers.CronTriggerImpl;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.support.ArgumentConvertingMethodInvoker;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.Schedules;
import org.springframework.scheduling.quartz.JobMethodInvocationFailedException;
import org.springframework.scheduling.quartz.MethodInvokingJobDetailFactoryBean;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.util.MethodInvoker;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class QuartzJobListener implements ApplicationListener<ContextRefreshedEvent> {
    @Autowired
    private Scheduler scheduler;

    @Autowired
    private AnnotationJobRepository jobRepository;

    @Autowired
    private ApplicationContext applicationContext;


    public void onApplicationEvent(ContextRefreshedEvent event) {
        try {
            if(this.applicationContext!=event.getApplicationContext()){
                return;
            }
            Map<JobDetail, CronTrigger> jobDetailCronTriggerMap = this.loadCronTriggerBeans(applicationContext);
            this.scheduleJobs(jobDetailCronTriggerMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Map<JobDetail, CronTrigger> loadCronTriggerBeans(ApplicationContext applicationContext) {
        Map<String, Object> quartzJobContainerMap = applicationContext.getBeansWithAnnotation(QuartzJobContainer.class);
        Map<JobDetail, CronTrigger> jobTriggerMap = new HashMap<JobDetail, CronTrigger>();

        for (Map.Entry<String, Object> entry : quartzJobContainerMap.entrySet()) {
            Object jobContainer = entry.getValue();
            try {
                QuartzJobContainer quartzJobContainerAnnotation = AnnotationUtils.findAnnotation(jobContainer.getClass(), QuartzJobContainer.class);
                Class<?> targetClass = AopUtils.getTargetClass(jobContainer);
                ReflectionUtils.doWithMethods(targetClass, new ReflectionUtils.MethodCallback() {
                    @Override
                    public void doWith(Method method) throws IllegalArgumentException, IllegalAccessException {
                        QuartzJob quartzJob = AnnotationUtils.getAnnotation(method, QuartzJob.class);
                        processQuartzJob(quartzJob, method, quartzJobContainerAnnotation);
                    }
                });
            }catch(Exception e){
                e.printStackTrace();
            }






            try {
                if (Job.class.isAssignableFrom(jobContainer.getClass())) {

                    CronTriggerImpl cronTriggerBean = new CronTriggerImpl();
                    cronTriggerBean.setCronExpression(quartzJobAnnotation.cronExp());
                    cronTriggerBean.setName(jobContainer.getClass().getName() + "_trigger");
                    cronTriggerBean.setJobGroup(Scheduler.DEFAULT_GROUP);
                    cronTriggerBean.setStartTime(new Date(System.currentTimeMillis() + quartzJobAnnotation.startDelayMills()));
                    cronTriggerBean.setTimeZone(TimeZone.getDefault());

                    JobDetailImpl jobDetail = new JobDetailImpl();
                    jobDetail.setName(jobContainer.getClass().getName());
                    jobDetail.setJobClass((Class<? extends Job>) jobContainer.getClass());

                    jobTriggerMap.put(jobDetail, cronTriggerBean);
                    jobRepository.putJob(jobContainer.getClass().getName(),(Job) jobContainer);
                } else {
                    throw new RuntimeException(jobContainer.getClass() + " doesn't implemented " + Job.class);
                }
            } catch (Exception e) {

            }

        }
        return jobTriggerMap;
    }

    private void  processQuartzJob(QuartzJob quartzJob, Method method,QuartzJobContainer quartzJobContainer){

        ArgumentConvertingMethodInvoker invoker = new ArgumentConvertingMethodInvoker();
        invoker.setTargetMethod(method.getName());
    }


    protected void scheduleJobs(Map<JobDetail, CronTrigger> jobDetailCronTriggerMap) {
        for (Map.Entry<JobDetail, CronTrigger> entry : jobDetailCronTriggerMap.entrySet()) {
            try {
                scheduler.scheduleJob(entry.getKey(), entry.getValue());
            } catch (SchedulerException e) {
                e.printStackTrace();
            }
        }
    }

    public static class MethodInvokingJob extends QuartzJobBean {
        private MethodInvoker methodInvoker;

        public MethodInvokingJob() {
        }

        public void setMethodInvoker(MethodInvoker methodInvoker) {
            this.methodInvoker = methodInvoker;
        }

        protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
            try {
                context.setResult(this.methodInvoker.invoke());
            } catch (InvocationTargetException var3) {
                if (var3.getTargetException() instanceof JobExecutionException) {
                    throw (JobExecutionException)var3.getTargetException();
                } else {
                    throw new JobMethodInvocationFailedException(this.methodInvoker, var3.getTargetException());
                }
            } catch (Exception var4) {
                throw new JobMethodInvocationFailedException(this.methodInvoker, var4);
            }
        }
    }
}
