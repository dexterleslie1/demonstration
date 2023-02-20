package com.future.demo;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class Config {
    public static final String BusinessExchangeName = "businessExchange"; // 交换器名称
    public static final String BusinessQueueName = "businessQueue";

    /**
     * 死信交换机
     */
    public static final String DEAD_EXCHANGE_NAME = "dead.exchange";
    /**
     * 死信队列
     */
    public static final String DEAD_QUEUE_NAME = "dead.queue";

    public static final String DEAD_ROUTING_KEY = "dead.routing.key";

    @Bean("businessQueue")
    public Queue queue(){
        Map<String, Object> args = new HashMap<>();
        //声明绑定的死信交换机
        args.put("x-dead-letter-exchange", DEAD_EXCHANGE_NAME);
        //声明死信路由key
        args.put("x-dead-letter-routing-key", DEAD_ROUTING_KEY);
        return QueueBuilder.durable(BusinessQueueName).autoDelete().withArguments(args).build();
    }

    // 配置默认的交互机
    @Bean("businessExchange")
    public AbstractExchange businessExchange(){
        Map<String,Object> args=new HashMap<>();
        args.put("x-delayed-type", "direct");
        // 参数二为类型：必须是x-delayed-message
        return new CustomExchange(BusinessExchangeName,"x-delayed-message",true,true,args);
    }

    // 绑定队列到交换器
    @Bean
    public Binding businessBinding(@Qualifier("businessQueue") Queue queue, @Qualifier("businessExchange") AbstractExchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with("routingKey1").noargs();
    }

    @Bean("deadQueue")
    public Queue deadQueue() {
        return QueueBuilder.durable(DEAD_QUEUE_NAME).autoDelete().build();
    }
    @Bean("deadExchange")
    public AbstractExchange deadExchange() {
        return new DirectExchange(DEAD_EXCHANGE_NAME, true, true);
    }
    @Bean
    public Binding deadBinding(@Qualifier("deadQueue") Queue queue, @Qualifier("deadExchange") AbstractExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(DEAD_ROUTING_KEY).noargs();
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
