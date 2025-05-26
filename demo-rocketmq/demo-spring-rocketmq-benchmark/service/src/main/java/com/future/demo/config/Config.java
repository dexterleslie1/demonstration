package com.future.demo.config;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.atomic.AtomicInteger;

@Configuration
public class Config {
    public final static String ProducerAndConsumerGroup = "demo-producer-and-consumer-group";
    public final static int TotalMessageCount = 2;

    @Value("${namesrvaddr}")
    String namesrvaddr;

    @Bean(initMethod = "start", destroyMethod = "shutdown")
    public DefaultMQProducer producer() {
        // 创建生产者实例，并设置生产者组名
        DefaultMQProducer producer = new DefaultMQProducer(ProducerAndConsumerGroup);
        // 设置 Name Server 地址，此处为示例，实际使用时请替换为真实的 Name Server 地址
        producer.setNamesrvAddr(this.namesrvaddr);
        // 设置重试次数
        producer.setRetryTimesWhenSendFailed(3);
        // 设置超时时间
        producer.setSendMsgTimeout(10000);
        return producer;
    }

    @Bean
    public AtomicInteger counter() {
        return new AtomicInteger();
    }
}
