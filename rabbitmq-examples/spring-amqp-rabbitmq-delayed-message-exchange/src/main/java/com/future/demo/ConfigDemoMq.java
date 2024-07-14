package com.future.demo;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Configuration
@Slf4j
public class ConfigDemoMq {
    public static final String BusinessPrefix = "demo-test-delayed";
    public static final String ExchangeName = BusinessPrefix + "-x";
    public static final String QueueName = BusinessPrefix + "-q";
    public static final String RoutingKey = "routingKey1";
    private static final String HeaderXMaxRetries = "x-max-retries";

    private final AtomicInteger counter = new AtomicInteger();

    @Autowired
    private AmqpTemplate rabbitTemplate;

    // concurrency = "5" 表示同时会有5个线程监听myQueue队列，并处理消息。
    @RabbitListener(queues = QueueName, concurrency = "1024")
    public void receiveMessage(String message, Message messageO) {
        try {
            log.info("Received <" + message + ">");

            counter.incrementAndGet();

            boolean b = true;
            if (b) {
                throw new Exception("测试异常");
            }
        } catch (Exception ex) {
            Integer xMaxRetries = messageO.getMessageProperties().getHeader(HeaderXMaxRetries);
            if (xMaxRetries == null) {
                xMaxRetries = 0;
            }

            messageO.getMessageProperties().setHeader(HeaderXMaxRetries, xMaxRetries + 1);

            log.info("x-max-retries={}", xMaxRetries);

            // 最多重试1次
            if (xMaxRetries < 1) {
                this.rabbitTemplate.convertAndSend(ExchangeName, RoutingKey, messageO, MessagePostProcessortVariable);
            }
        }
    }

    final static Integer Delay = 3000;
    public final static MessagePostProcessor MessagePostProcessortVariable = message -> {
        message.getMessageProperties().setDelay(Delay);
        return message;
    };

    public int getCount() {
        return this.counter.get();
    }

    public void clear() {
        this.counter.set(0);
    }

    @Bean
    public Queue queue() {
        return QueueBuilder.nonDurable(QueueName).build();
    }

    // 配置默认的交互机
    @Bean
    public CustomExchange customExchange() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-delayed-type", "direct");
        // 参数二为类型：必须是x-delayed-message
        // durable（持久化）:
        //      当durable设置为true时，交换器将被创建为持久的。这意味着即使RabbitMQ服务器重启，交换器也会继续存在。这对于需要确保消息不会在RabbitMQ服务器重启时丢失的系统来说非常重要。
        //      如果durable设置为false，交换器将是非持久的。这意味着一旦RabbitMQ服务器重启，交换器（以及可能的所有绑定和队列，如果这些也是非持久的）将被删除，且不会重新创建。
        // autoDelete（自动删除）:
        //      当autoDelete设置为true时，如果交换器不再被任何队列绑定，它将被自动删除。这个选项通常用于那些仅在特定情况下才需要的临时交换器，或者当你想要确保在没有队列绑定时自动清理交换器时。
        //      如果autoDelete设置为false（默认值），交换器将不会被自动删除，即使没有任何队列与之绑定。
        return new CustomExchange(ExchangeName, "x-delayed-message", true, false, args);
    }

    // 绑定队列到交换器
    @Bean
    public Binding binding(Queue queue, CustomExchange customExchange) {
        return BindingBuilder.bind(queue).to(customExchange).with(RoutingKey).noargs();
    }
}
