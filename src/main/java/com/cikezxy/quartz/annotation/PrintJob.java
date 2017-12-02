package com.cikezxy.quartz.annotation;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.util.Date;

@QuartzJob(cronExp = "0/5 * * ? * MON-SUN")
public class PrintJob extends QuartzJobBean{

    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        System.out.println("print job:"+new Date());
    }
}
