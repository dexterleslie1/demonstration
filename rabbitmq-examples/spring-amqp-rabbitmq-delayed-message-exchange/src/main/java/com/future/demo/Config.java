package com.future.demo;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class Config {
    public static final String ExchangeName ="myDelayExchange"; // 交换器名称
    public static final String QueueName = "myDelayedQueue";

    @Bean
    public Queue queue(){
        return QueueBuilder.durable(Config.QueueName).autoDelete().build();
    }

    // 配置默认的交互机
    @Bean
    public CustomExchange customExchange(){
        Map<String,Object> args=new HashMap<>();
        args.put("x-delayed-type", "direct");
        // 参数二为类型：必须是x-delayed-message
        return new CustomExchange(ExchangeName,"x-delayed-message",true,true, args);
    }

    // 绑定队列到交换器
    @Bean
    public Binding binding(Queue queue, CustomExchange customExchange){
        return BindingBuilder.bind(queue).to(customExchange).with("routingKey1").noargs();
    }
}
