package com.cikezxy.quartz.annotation;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.JobMethodInvocationFailedException;
import org.springframework.util.MethodInvoker;

import java.lang.reflect.InvocationTargetException;

public class MethodInvokingJob implements Job{

    private MethodInvoker methodInvoker;

    public MethodInvokingJob(MethodInvoker invoker) {
        this.methodInvoker = invoker;
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        try {
            context.setResult(this.methodInvoker.invoke());
        } catch (InvocationTargetException var3) {
            if (var3.getTargetException() instanceof JobExecutionException) {
                throw (JobExecutionException) var3.getTargetException();
            } else {
                throw new JobMethodInvocationFailedException(this.methodInvoker, var3.getTargetException());
            }
        } catch (Exception var4) {
            throw new JobMethodInvocationFailedException(this.methodInvoker, var4);
        }
    }
}
