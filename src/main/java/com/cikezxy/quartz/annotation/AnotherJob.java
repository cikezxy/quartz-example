package com.cikezxy.quartz.annotation;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.util.Date;

@QuartzJob(cronExp = "0/6 * * ? * MON-SUN")
public class AnotherJob extends QuartzJobBean{

    @Autowired
    private Counter counter;
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        System.out.println("Another Job:"+new Date()+"Counter="+counter.increment());
    }
}
