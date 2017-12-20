package com.cikezxy.quartz.annotation;

import org.quartz.Job;
import org.quartz.JobDetail;
import org.springframework.stereotype.Component;
import org.springframework.util.MethodInvoker;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class QuartzJobRepository {

    private ConcurrentHashMap<JobDetail, MethodInvoker> repo = new ConcurrentHashMap<JobDetail, MethodInvoker>();

    public MethodInvoker getJob(JobDetail jobDetail) {
        return repo.get(jobDetail);
    }

    public void putJob(JobDetail jobDetail, MethodInvoker job) {
        repo.put(jobDetail, job);
    }
}
