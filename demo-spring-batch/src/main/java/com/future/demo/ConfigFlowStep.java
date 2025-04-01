package com.future.demo;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 演示流式步骤，流式步骤是用于一个步骤中有多个子步骤的场景
 */
@Configuration
public class ConfigFlowStep {

    public final static String Prefix = "flowStep";
    public final Map<String, Object> Counter = new ConcurrentHashMap<>();
    @Resource
    StepBuilderFactory stepBuilderFactory;
    @Resource
    JobBuilderFactory jobBuilderFactory;

    @Bean(Prefix + "TaskletA")
    public Tasklet taskletA() {
        return new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
                String flag = "TaskletA";
                if (!Counter.containsKey(flag)) {
                    Counter.put(flag, 0);
                }
                Counter.put(flag, (Integer) Counter.get(flag) + 1);

                return RepeatStatus.FINISHED;
            }
        };
    }

    @Bean(Prefix + "TaskletB1")
    public Tasklet taskletB1() {
        return new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
                String flag = "TaskletB1";
                if (!Counter.containsKey(flag)) {
                    Counter.put(flag, 0);
                }
                Counter.put(flag, (Integer) Counter.get(flag) + 1);

                return RepeatStatus.FINISHED;
            }
        };
    }

    @Bean(Prefix + "TaskletB2")
    public Tasklet taskletB2() {
        return new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
                String flag = "TaskletB2";
                if (!Counter.containsKey(flag)) {
                    Counter.put(flag, 0);
                }
                Counter.put(flag, (Integer) Counter.get(flag) + 1);

                return RepeatStatus.FINISHED;
            }
        };
    }

    @Bean(Prefix + "TaskletB3")
    public Tasklet taskletB3() {
        return new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
                String flag = "TaskletB3";
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
                String flag = "TaskletC";
                if (!Counter.containsKey(flag)) {
                    Counter.put(flag, 0);
                }
                Counter.put(flag, (Integer) Counter.get(flag) + 1);

                return RepeatStatus.FINISHED;
            }
        };
    }

    @Bean(Prefix + "StepB1")
    public Step stepB1() {
        return this.stepBuilderFactory.get(Prefix + "StepB1").tasklet(taskletB1()).build();
    }

    @Bean(Prefix + "StepB2")
    public Step stepB2() {
        return this.stepBuilderFactory.get(Prefix + "StepB2").tasklet(taskletB2()).build();
    }

    @Bean(Prefix + "StepB3")
    public Step stepB3() {
        return this.stepBuilderFactory.get(Prefix + "StepB3").tasklet(taskletB3()).build();
    }

    // FlowB包含多个StepB的子步骤StepB1、StepB2、StepB3
    @Bean(Prefix + "FlowB")
    Flow flowB() {
        return new FlowBuilder<Flow>(Prefix + "FlowB")
                .start(this.stepB1())
                .next(this.stepB2())
                .next(this.stepB3())
                .build();
    }

    @Bean(Prefix + "StepA")
    public Step stepA() {
        return this.stepBuilderFactory.get(Prefix + "StepA").tasklet(taskletA()).build();
    }

    // StepB中包含一个flowB
    @Bean(Prefix + "StepB")
    public Step stepB() {
        return this.stepBuilderFactory.get(Prefix + "StepB").flow(this.flowB()).build();
    }

    @Bean(Prefix + "StepC")
    public Step stepC() {
        return this.stepBuilderFactory.get(Prefix + "StepC").tasklet(taskletC()).build();
    }

    @Bean(Prefix + "Job")
    public Job job() {
        return this.jobBuilderFactory.get(Prefix + "Job")
                .start(this.stepA())
                .next(this.stepB())
                .next(this.stepC())
                .build();
    }

}
