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
 * 配置作业执行状态监听器相关
 */
@Configuration
public class ConfigJobExecutionListener {

    public final static String Prefix = "jobExecutionListener";

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
                Counter++;

                // 如果测试RepeatStatus并且Counter<=1则返回RepeatStatus.CONTINUABLE让step继续重复执行
                boolean ifTestRepeatStatus = chunkContext.getStepContext().getJobParameters().get("ifTestRepeatStatus") != null;
                if(ifTestRepeatStatus && Counter <= 1) {
                    return RepeatStatus.CONTINUABLE;
                }
                return RepeatStatus.FINISHED;
            }
        };
    }

    @Bean(Prefix + "Step")
    public Step step() {
        return this.stepBuilderFactory.get(Prefix + "Step").tasklet(tasklet()).build();
    }

    @Bean
    JobExecutionListener jobExecutionListener() {
        return new MyJobExecutionListener();
    }

    @Bean(Prefix + "Job")
    public Job job() {
        return this.jobBuilderFactory.get(Prefix + "Job").start(step()).listener(jobExecutionListener()).build();
    }

}
