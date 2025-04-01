package com.future.demo;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * 配置作业参数增量器相关
 */
@Configuration
public class ConfigRunIdIncrementer {

    public final static String Prefix = "jobParametersIncrementer";

    @Resource
    StepBuilderFactory stepBuilderFactory;
    @Resource
    JobBuilderFactory jobBuilderFactory;

    /**
     * 用于单元测试查看任务是否被执行指定次数
     */
    public int Counter = 0;

    @Bean(Prefix + "Tasklet")
    public Tasklet tasklet() {
        return new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
                Object runId = chunkContext.getStepContext().getJobParameters().get("run.id");
                System.out.println("作业参数增量器参数run.id=" + runId);
                Counter++;
                return RepeatStatus.FINISHED;
            }
        };
    }

    @Bean(Prefix + "Step")
    public Step step() {
        return this.stepBuilderFactory.get(Prefix + "Step").tasklet(tasklet()).build();
    }

    @Bean(Prefix + "Job")
    public Job job() {
        return this.jobBuilderFactory.get(Prefix + "Job").start(step()).incrementer(new RunIdIncrementer()).build();
    }

}
