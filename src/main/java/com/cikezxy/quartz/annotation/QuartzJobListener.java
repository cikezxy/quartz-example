package com.cikezxy.quartz.annotation;

import org.quartz.*;
import org.quartz.impl.JobDetailImpl;
import org.quartz.impl.triggers.CronTriggerImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.annotation.AnnotationUtils;

import java.util.*;

public class QuartzJobListener implements ApplicationListener<ContextRefreshedEvent> {
    @Autowired
    private Scheduler scheduler;

    @Autowired
    private AnnotationJobRepository jobRepository;


    public void onApplicationEvent(ContextRefreshedEvent event) {
        try {
            ApplicationContext applicationContext = event.getApplicationContext();
            Map<JobDetail, CronTrigger> jobDetailCronTriggerMap = this.loadCronTriggerBeans(applicationContext);
            this.scheduleJobs(jobDetailCronTriggerMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Map<JobDetail, CronTrigger> loadCronTriggerBeans(ApplicationContext applicationContext) {
        Map<String, Object> quartzJobBeans = applicationContext.getBeansWithAnnotation(QuartzJob.class);
        Map<JobDetail, CronTrigger> jobTriggerMap = new HashMap<JobDetail, CronTrigger>();

        for (Map.Entry<String, Object> entry : quartzJobBeans.entrySet()) {
            Object job = entry.getValue();
            try {
                QuartzJob quartzJobAnnotation = AnnotationUtils.findAnnotation(job.getClass(), QuartzJob.class);
                if (Job.class.isAssignableFrom(job.getClass())) {

                    CronTriggerImpl cronTriggerBean = new CronTriggerImpl();
                    cronTriggerBean.setCronExpression(quartzJobAnnotation.cronExp());
                    cronTriggerBean.setName(job.getClass().getName() + "_trigger");
                    cronTriggerBean.setJobGroup(Scheduler.DEFAULT_GROUP);
                    cronTriggerBean.setStartTime(new Date(System.currentTimeMillis() + quartzJobAnnotation.startDelayMills()));
                    cronTriggerBean.setTimeZone(TimeZone.getDefault());

                    JobDetailImpl jobDetail = new JobDetailImpl();
                    jobDetail.setName(job.getClass().getName());
                    jobDetail.setJobClass((Class<? extends Job>) job.getClass());

                    jobTriggerMap.put(jobDetail, cronTriggerBean);
                    jobRepository.putJob(job.getClass().getName(),(Job) job);
                } else {
                    throw new RuntimeException(job.getClass() + " doesn't implemented " + Job.class);
                }
            } catch (Exception e) {

            }

        }
        return jobTriggerMap;
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
}
