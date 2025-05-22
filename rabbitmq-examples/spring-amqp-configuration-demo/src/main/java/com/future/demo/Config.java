package com.future.demo;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Dexterleslie.Chan
 */
@Configuration
public class Config {
    // 交换机名称
    public static final String topicExchangeName = "spring-boot-exchange";
    // 队列名称
    static final String queueName = "spring-boot";

    // 创建队列
    @Bean
    Queue queue() {
        return new Queue(queueName, false, false, true);
    }

    // 创建交换机
    @Bean
    TopicExchange exchange() {
        return new TopicExchange(topicExchangeName, false, true);
    }

    // 绑定队列到交换机
    @Bean
    Binding binding(Queue queue, TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with("foo.bar.*");
    }

    @Bean
    SimpleMessageListenerContainer container(ConnectionFactory connectionFactory,
                                             MessageListenerAdapter listenerAdapter) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(queueName);
        container.setMessageListener(listenerAdapter);
        return container;
    }

    @Bean
    MessageListenerAdapter listenerAdapter(Receiver receiver) {
        return new MessageListenerAdapter(receiver, "receiveMessage");
    }
}
