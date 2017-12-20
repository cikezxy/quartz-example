package com.cikezxy.quartz.annotation;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Task {

    @Autowired
    private Counter counter;


    @QuartzJob(cron = "0/5 * * ? * MON-SUN",group = "aaa")
    public void one() {
        System.out.println("one invoked:" + counter.increment());
    }

    @QuartzJob(cron = "0/5 * * ? * MON-SUN", priority = 10)
    public void two() {
        System.out.println("two invoked:" + counter.increment());
    }
}
