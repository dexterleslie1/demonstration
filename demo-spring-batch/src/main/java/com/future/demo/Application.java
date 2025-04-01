package com.future.demo;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersValidator;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.CompositeJobParametersValidator;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import javax.annotation.Resource;
import java.util.Arrays;

/**
 * @author Dexterleslie.Chan
 */
@SpringBootApplication
// 此注解用于创建springbatch相关bean，能够在应用中@Autowired这些相关bean
@EnableBatchProcessing
public class Application {

    @Resource
    StepBuilderFactory stepBuilderFactory;
    @Resource
    JobBuilderFactory jobBuilderFactory;

    /**
     * @param args
     */
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    @Qualifier("tasklet")
    public Tasklet tasklet() {
        return new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
                // 获取作业参数param1
                String param1 = (String)chunkContext.getStepContext().getJobParameters().get("param1");
                System.out.println("作业" + chunkContext.getStepContext().getJobName() + "参数param1=" + param1);
                return RepeatStatus.FINISHED;
            }
        };
    }

    @Bean
    @Qualifier("step")
    public Step step() {
        return this.stepBuilderFactory.get("step").tasklet(tasklet()).build();
    }

    /**
     * 组合参数校验器，用于验证param1、param2
     *
     * @return
     */
    @Bean
    @Qualifier("compositeJobParametersValidator")
    CompositeJobParametersValidator compositeJobParametersValidator() {
        CompositeJobParametersValidator compositeJobParametersValidator = new CompositeJobParametersValidator();
        compositeJobParametersValidator.setValidators(Arrays.asList(new Param1JobParametersValidator(), new Param2JobParametersValidator()));
        return compositeJobParametersValidator;
    }

    @Bean
    @Qualifier("job")
    public Job job() {
        return this.jobBuilderFactory.get("job").validator(compositeJobParametersValidator()).start(step()).build();
    }

}
