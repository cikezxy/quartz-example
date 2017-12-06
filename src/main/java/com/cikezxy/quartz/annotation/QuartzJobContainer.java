package com.cikezxy.quartz.annotation;

import org.quartz.Scheduler;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface QuartzJobContainer {

    String group() default Scheduler.DEFAULT_GROUP;
}
