package com.future.demo;

import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Dexterleslie.Chan
 */
@Configuration
public class Config {

    public static final String exchangeName = "demo-spring-amqp-configuration-exchange";
    public static final String queueName = "demo-spring-amqp-configuration-queue";
    public static final int TotalMessageCount = 1024;

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setConcurrentConsumers(3);
        factory.setMaxConcurrentConsumers(10);
        // 手动ack模式
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);

        // 设置批量处理模式，注意：批量处理模式只支持使用Java配置
        // https://docs.spring.io/spring-amqp/reference/amqp/receiving-messages/batch.html
        // 启用批量处理
        factory.setBatchListener(true);
        // 每个批次最大消息数
        factory.setBatchSize(256);
        // 启用消费者端批量处理
        factory.setConsumerBatchEnabled(true);

        return factory;
    }

    @Bean
    AtomicInteger counter() {
        return new AtomicInteger();
    }
}
