package com.future.demo;

import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 演示步骤监听器用法
 */
@Configuration
public class ConfigStepExecutionListener {

    public final static String Prefix = "stepExecutionListener";

    @Resource
    StepBuilderFactory stepBuilderFactory;
    @Resource
    JobBuilderFactory jobBuilderFactory;

    public final AtomicInteger CounterBeforeStep = new AtomicInteger();
    public final AtomicInteger CounterAfterStep = new AtomicInteger();

    @Bean(Prefix + "Tasklet")
    public Tasklet tasklet() {
        return new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
                return RepeatStatus.FINISHED;
            }
        };
    }

    @Bean(Prefix + "Step")
    public Step step() {
        return this.stepBuilderFactory.get(Prefix + "Step").tasklet(tasklet()).listener(new StepExecutionListener() {
            @Override
            public void beforeStep(StepExecution stepExecution) {
                ConfigStepExecutionListener.this.CounterBeforeStep.incrementAndGet();
            }

            @Override
            public ExitStatus afterStep(StepExecution stepExecution) {
                ConfigStepExecutionListener.this.CounterAfterStep.incrementAndGet();
                return stepExecution.getExitStatus();
            }
        }).build();
    }

    @Bean(Prefix + "Job")
    public Job job() {
        return this.jobBuilderFactory.get(Prefix + "Job").start(step()).build();
    }

}
