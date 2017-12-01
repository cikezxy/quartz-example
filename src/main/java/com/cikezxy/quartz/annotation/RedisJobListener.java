package com.cikezxy.quartz.annotation;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;
import org.springframework.stereotype.Component;

@Component
public class RedisJobListener implements JobListener{

    public void jobToBeExecuted(JobExecutionContext context) {
        System.out.println("redis lock! nextTriggerTime=" + context.getNextFireTime());

    }

    public void jobExecutionVetoed(JobExecutionContext context) {

    }

    public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
        System.out.println("redis unlock!");
    }

    public String getName() {
        return "redis listener";
    }
}
