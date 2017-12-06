package com.cikezxy.quartz.annotation;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.util.Date;

public class PrintJob extends QuartzJobBean{

    @Autowired
    private Counter counter;

    @QuartzJob(cronExp = "0/5 * * ? * MON-SUN")
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        System.out.println("print job:"+new Date()+"Counter="+counter.increment());
    }
}
