package com.websystique.spring.quartz;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.util.concurrent.ConcurrentHashMap;

public abstract  class QuartzJobBeanUtil extends QuartzJobBean{

    private static ConcurrentHashMap<String, JobExecutionContext> contextMap = new ConcurrentHashMap<String, JobExecutionContext>();

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        contextMap.put(this.getClass().getName(),context);
        executeJob(context);
    }

    protected abstract void executeJob(JobExecutionContext context);

    public static JobExecutionContext getQuartzJobContext(String key){
        return contextMap.get(key);
    }
}
