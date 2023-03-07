package com.future.demo;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
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

    @RabbitListener(queues = ConfigDemoMq.QueueName)
    public void receiveMessage(String message, Message messageO) {
        try {
            log.info("Received <" + message + ">");

            TimeUnit.MILLISECONDS.sleep(10);

            counter.incrementAndGet();

            boolean b = true;
            if(b) {
                throw new Exception("测试异常");
            }
        } catch (Exception ex) {
            Integer xMaxRetries = messageO.getMessageProperties().getHeader(HeaderXMaxRetries);
            if(xMaxRetries == null) {
                xMaxRetries = 0;
            }

            messageO.getMessageProperties().setHeader(HeaderXMaxRetries, xMaxRetries + 1);

            log.info("x-max-retries={}", xMaxRetries);

            // 最多重试1次
            if(xMaxRetries < 1) {
                this.rabbitTemplate.convertAndSend(ConfigDemoMq.ExchangeName, ConfigDemoMq.RoutingKey, messageO, MessagePostProcessortVariable);
            }
        }
    }

    final static long Delay = 3000;
    public final static MessagePostProcessor MessagePostProcessortVariable = message -> {
        message.getMessageProperties().setHeader("x-delay", Delay);
        return message;
    };

    public int getCount() {
        return this.counter.get();
    }

    @Bean
    public Queue queue() {
        return QueueBuilder.nonDurable(ConfigDemoMq.QueueName).build();
    }

    // 配置默认的交互机
    @Bean
    public CustomExchange customExchange() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-delayed-type", "direct");
        // 参数二为类型：必须是x-delayed-message
        return new CustomExchange(ExchangeName, "x-delayed-message", false, false, args);
    }

    // 绑定队列到交换器
    @Bean
    public Binding binding(Queue queue, CustomExchange customExchange) {
        return BindingBuilder.bind(queue).to(customExchange).with(RoutingKey).noargs();
    }
}
