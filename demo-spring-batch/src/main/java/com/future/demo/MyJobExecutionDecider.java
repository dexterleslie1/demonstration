package com.future.demo;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;

public class MyJobExecutionDecider implements JobExecutionDecider {
    @Override
    public FlowExecutionStatus decide(JobExecution jobExecution, StepExecution stepExecution) {
        // 根据作业输入的参数控制执行不同的步骤
        String stepType = jobExecution.getJobParameters().getString("stepType");
        if("A".equals(stepType)) {
            return new FlowExecutionStatus("A");
        } else if("B".equals(stepType)) {
            return new FlowExecutionStatus("B");
        } else {
            return new FlowExecutionStatus("C");
        }
    }
}
