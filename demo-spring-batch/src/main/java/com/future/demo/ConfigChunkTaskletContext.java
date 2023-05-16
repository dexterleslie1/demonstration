package com.future.demo;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 演示chunk tasklet用法
 */
@Configuration
public class ConfigChunkTaskletContext {

    public final static String Prefix = "chunkTasklet";
    private final static AtomicInteger TotalItemSize = new AtomicInteger(6);
    private final static int ChunkSize = 3;
    public List<String> itemReaderList = Collections.synchronizedList(new ArrayList<>());
    public List<String> itemProcessorList = Collections.synchronizedList(new ArrayList<>());
    public List<List<String>> itemWriterList = Collections.synchronizedList(new ArrayList<>());
    @Resource
    StepBuilderFactory stepBuilderFactory;
    @Resource
    JobBuilderFactory jobBuilderFactory;

    @Bean(Prefix + "ItemReader")
    public ItemReader<String> itemReader() {
        return new ItemReader<String>() {
            @Override
            public String read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
                // 处理完指定的item数量才停止读取
                int index = TotalItemSize.getAndDecrement();
                if (index > 0) {
                    String item = "item" + index;
                    ConfigChunkTaskletContext.this.itemReaderList.add(item);
                    return item;
                } else {
                    // 如果不返回null，程序不会停止重复无限读取数据并处理
                    return null;
                }
            }
        };
    }

    @Bean
    public ItemProcessor<String, String> itemProcessor() {
        return new ItemProcessor<String, String>() {
            @Override
            public String process(String s) throws Exception {
                s = s + "-processed";
                ConfigChunkTaskletContext.this.itemProcessorList.add(s);
                return s;
            }
        };
    }

    @Bean
    public ItemWriter<String> itemWriter() {
        return new ItemWriter<String>() {
            @Override
            public void write(List<? extends String> list) throws Exception {
                List<String> tempList = new ArrayList<>(list);
                ConfigChunkTaskletContext.this.itemWriterList.add(tempList);
            }
        };
    }

    @Bean(Prefix + "Step")
    public Step step() {
        return this.stepBuilderFactory.get(Prefix + "Step1")
                // 启动3个chunk，每个chunk都执行一次 itemReader->itemProcessor->itemWriter流程
                .<String, String>chunk(ChunkSize)
                .reader(itemReader())
                .processor(itemProcessor())
                .writer(itemWriter())
                .build();
    }

    @Bean(Prefix + "Job")
    public Job job() {
        return this.jobBuilderFactory.get(Prefix + "Job").start(step()).build();
    }

}
