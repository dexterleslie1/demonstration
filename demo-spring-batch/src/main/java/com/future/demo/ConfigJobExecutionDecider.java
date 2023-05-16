package com.future.demo;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 演示步骤流程控制，根据不同的条件执行不同的步骤
 */
@Configuration
public class ConfigJobExecutionDecider {

    public final static String Prefix = "jobExecutionDecider";
    public final Map<String, Object> Counter = new ConcurrentHashMap<>();
    @Resource
    StepBuilderFactory stepBuilderFactory;
    @Resource
    JobBuilderFactory jobBuilderFactory;

    @Bean(Prefix + "TaskletFirst")
    public Tasklet taskletFirst() {
        return new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
                String flag = "First";
                if (!Counter.containsKey(flag)) {
                    Counter.put(flag, 0);
                }
                Counter.put(flag, (Integer) Counter.get(flag) + 1);

                return RepeatStatus.FINISHED;
            }
        };
    }

    @Bean(Prefix + "TaskletA")
    public Tasklet taskletA() {
        return new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
                String flag = "A";
                if (!Counter.containsKey(flag)) {
                    Counter.put(flag, 0);
                }
                Counter.put(flag, (Integer) Counter.get(flag) + 1);

                return RepeatStatus.FINISHED;
            }
        };
    }

    @Bean(Prefix + "TaskletB")
    public Tasklet taskletB() {
        return new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
                String flag = "B";
                if (!Counter.containsKey(flag)) {
                    Counter.put(flag, 0);
                }
                Counter.put(flag, (Integer) Counter.get(flag) + 1);

                return RepeatStatus.FINISHED;
            }
        };
    }

    @Bean(Prefix + "TaskletC")
    public Tasklet taskletC() {
        return new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
                String flag = "C";
                if (!Counter.containsKey(flag)) {
                    Counter.put(flag, 0);
                }
                Counter.put(flag, (Integer) Counter.get(flag) + 1);

                return RepeatStatus.FINISHED;
            }
        };
    }

    @Bean(Prefix + "StepFirst")
    public Step stepFirst() {
        return this.stepBuilderFactory.get(Prefix + "StepFirst").tasklet(taskletFirst()).build();
    }

    @Bean(Prefix + "StepA")
    public Step stepA() {
        return this.stepBuilderFactory.get(Prefix + "StepA").tasklet(taskletA()).build();
    }

    @Bean(Prefix + "StepB")
    public Step stepB() {
        return this.stepBuilderFactory.get(Prefix + "StepB").tasklet(taskletB()).build();
    }

    @Bean(Prefix + "StepC")
    public Step stepC() {
        return this.stepBuilderFactory.get(Prefix + "StepC").tasklet(taskletC()).build();
    }

    @Bean(Prefix + "MyJobExecutionDecider")
    JobExecutionDecider myJobExecutionDecider() {
        return new MyJobExecutionDecider();
    }

    @Bean(Prefix + "Job")
    public Job job() {
        return this.jobBuilderFactory.get(Prefix + "Job")
                .start(stepFirst())
                .next(this.myJobExecutionDecider())
                .from(this.myJobExecutionDecider()).on("A").to(stepA())
                .from(this.myJobExecutionDecider()).on("B").to(stepB())
                .from(this.myJobExecutionDecider()).on("C").to(stepC())
                .end()
                .build();
    }

}
