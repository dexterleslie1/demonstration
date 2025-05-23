package com.future.demo;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Dexterleslie.Chan
 */
@Configuration
public class Config {

    public static final String exchangeName = "spring-amqp-annotaion-rabbitlistener-exchange-demo";
    static final String queueName = "spring-amqp-annotaion-rabbitlistener-queue-demo";

    /*@Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setConcurrentConsumers(3);
        factory.setMaxConcurrentConsumers(10);
        // 手动ack模式
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        return factory;
    }*/

    @Bean
    AtomicInteger counter() {
        return new AtomicInteger();
    }
}
