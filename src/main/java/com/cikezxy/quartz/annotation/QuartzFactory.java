package com.cikezxy.quartz.annotation;

import org.quartz.Job;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.spi.JobFactory;
import org.quartz.spi.TriggerFiredBundle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class QuartzFactory implements JobFactory {

    @Autowired
    QuartzJobRepository repo;

    @Override
    public Job newJob(TriggerFiredBundle bundle, Scheduler scheduler) throws SchedulerException {
        return new MethodInvokingJob(repo.getJob(bundle.getJobDetail()));
    }
}
