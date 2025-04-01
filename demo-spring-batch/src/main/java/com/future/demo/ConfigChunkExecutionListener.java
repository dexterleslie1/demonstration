package com.future.demo;

import org.springframework.batch.core.ChunkListener;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.item.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 演示chunk监听器用法
 */
@Configuration
public class ConfigChunkExecutionListener {

    public final static String Prefix = "chunkExecutionListener";

    public final AtomicInteger CounterBeforeChunk = new AtomicInteger();
    public final AtomicInteger CounterAfterChunk = new AtomicInteger();
    public final AtomicInteger CounterAfterChunkError = new AtomicInteger();

    @Resource
    StepBuilderFactory stepBuilderFactory;
    @Resource
    JobBuilderFactory jobBuilderFactory;

    @Bean(Prefix + "ItemReader")
    public ItemReader<String> itemReader() {
        AtomicInteger counter = new AtomicInteger(3);
        return new ItemReader<String>() {
            @Override
            public String read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
                int index = counter.decrementAndGet();
                if (index < 0) {
                    return null;
                } else {
                    return "item" + index;
                }
            }
        };
    }

    @Bean(Prefix + "ItemProcessor")
    public ItemProcessor<String, String> itemProcessor() {
        return new ItemProcessor<String, String>() {
            @Override
            public String process(String item) throws Exception {
                if (item.equals("item1")) {
                    throw new Exception("测试异常" + item);
                }
                return item + "-processed";
            }
        };
    }

    @Bean(Prefix + "ItemWriter")
    public ItemWriter<String> itemWriter() {
        return new ItemWriter<String>() {
            @Override
            public void write(List<? extends String> items) throws Exception {
                // NOTE: 这里可以不执行任何逻辑
            }
        };
    }

    @Bean(Prefix + "Step")
    public Step step() {
        return this.stepBuilderFactory.get(Prefix + "Step")
                .<String, String>chunk(2)
                .reader(this.itemReader())
                .processor(this.itemProcessor())
                .writer(this.itemWriter())
                .listener(new ChunkListener() {
                    @Override
                    public void beforeChunk(ChunkContext context) {
                        ConfigChunkExecutionListener.this.CounterBeforeChunk.incrementAndGet();
                    }

                    @Override
                    public void afterChunk(ChunkContext context) {
                        ConfigChunkExecutionListener.this.CounterAfterChunk.incrementAndGet();
                    }

                    @Override
                    public void afterChunkError(ChunkContext context) {
                        ConfigChunkExecutionListener.this.CounterAfterChunkError.incrementAndGet();
                    }
                }).build();
    }

    @Bean(Prefix + "Job")
    public Job job() {
        return this.jobBuilderFactory.get(Prefix + "Job").start(step()).build();
    }

}
