package com.cikezxy.quartz.annotation;

import org.quartz.Job;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.spi.JobFactory;
import org.quartz.spi.TriggerFiredBundle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AnnotationJobFactory implements JobFactory{

    @Autowired
    AnnotationJobRepository repo;

    public Job newJob(TriggerFiredBundle bundle, Scheduler scheduler) throws SchedulerException {
        System.out.println("job factory invoked");
        return repo.getJob(bundle.getJobDetail().getJobClass().getName());
    }
}
