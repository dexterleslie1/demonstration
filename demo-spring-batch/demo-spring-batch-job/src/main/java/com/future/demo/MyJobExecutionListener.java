package com.future.demo;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;

public class MyJobExecutionListener implements JobExecutionListener {

    public int CounterBeforeJob;
    public int CounterAfterJob;

    @Override
    public void beforeJob(JobExecution jobExecution) {
        CounterBeforeJob++;
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        CounterAfterJob++;
    }

}
