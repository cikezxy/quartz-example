package com.cikezxy.quartz.annotation;


import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.quartz.Scheduler;
import org.springframework.stereotype.Component;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface QuartzJob {
    String cron();
    int priority() default 5;
    long startDelayMills() default 0;
    String group() default Scheduler.DEFAULT_GROUP;
}
