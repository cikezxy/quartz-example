package com.cikezxy.quartz.annotation;

import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;

@Component
public class Counter {

    private AtomicLong counter = new AtomicLong(0);

    public long increment(){
        return counter.getAndIncrement();
    }
}
