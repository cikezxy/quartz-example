package com.cikezxy.quartz.annotation;

import org.quartz.Job;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class AnnotationJobRepository {

    private ConcurrentHashMap<String ,Job> repo = new ConcurrentHashMap<String,Job>();

    public Job getJob(String key){
        return repo.get(key);
    }

    public Job putJob(String key, Job value){
        return repo.putIfAbsent(key,value);
    }
}
