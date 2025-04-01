package com.future.demo;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * 作业上下文演示配置
 */
@Configuration
public class ConfigJobContext {

    public final static String Prefix = "jobContext";

    @Resource
    StepBuilderFactory stepBuilderFactory;
    @Resource
    JobBuilderFactory jobBuilderFactory;

    /**
     * 表示stepContext之间是否共享数据
     */
    public boolean isStepContextValueShared = false;
    /**
     * 表示jobContext之间是否共享数据
     */
    public boolean isJobContextValueShared = false;

    @Bean(Prefix + "Tasklet1")
    public Tasklet tasklet1() {
        return new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
                // 保存数据到StepContext中，step之间不能共享
                chunkContext.getStepContext().getStepExecution().getExecutionContext().put("key-step1-step", "value-step1-step");
                // 保存数据到JobContext中，step之间能够共享
                chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext().put("key-step1-job", "value-step1-job");
                return RepeatStatus.FINISHED;
            }
        };
    }

    @Bean(Prefix + "Tasklet2")
    public Tasklet tasklet2() {
        return new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
                // 获取stepContext中数据，预期为null，因为通过stepContext不能够实现step之间数据共享
                Object object = chunkContext.getStepContext().getStepExecution().getExecutionContext().get("key-step1-step");
                isStepContextValueShared = object != null;

                // 获取jobContext中数据，预期不为null，因为通过jobContext能够实现step之间数据共享
                object = chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext().get("key-step1-job");
                isJobContextValueShared = object != null;

                return RepeatStatus.FINISHED;
            }
        };
    }

    @Bean(Prefix + "Step1")
    public Step step1() {
        return this.stepBuilderFactory.get(Prefix + "Step1").tasklet(tasklet1()).build();
    }

    @Bean(Prefix + "Step2")
    public Step step2() {
        return this.stepBuilderFactory.get(Prefix + "Step2").tasklet(tasklet2()).build();
    }

    @Bean(Prefix + "Job")
    public Job job() {
        return this.jobBuilderFactory.get(Prefix + "Job").start(step1()).next(step2()).build();
    }

}
