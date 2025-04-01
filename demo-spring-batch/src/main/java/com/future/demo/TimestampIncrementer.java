package com.future.demo;

import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersIncrementer;

import java.util.Date;

public class TimestampIncrementer implements JobParametersIncrementer {

    @Override
    public JobParameters getNext(JobParameters parameters) {
        return new JobParametersBuilder()
                .addLong("timestampP", new Date().getTime())
                .toJobParameters();
    }

}
