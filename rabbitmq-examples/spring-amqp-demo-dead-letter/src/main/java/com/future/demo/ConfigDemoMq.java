package com.future.demo;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

// 死信队列使用原理就是绑定死信队列到正常业务队列
@Configuration
@Slf4j
public class ConfigDemoMq {
    // !!!!!只需要修改这里，下面不需要修改!!!!!所属业务前缀，例如：订单服务order-creation
    public static final String BusinessPrefix = "demo-test-dl";

    // 以下配置不需要需要修改
    public static final String HeaderXMaxRetries = "x-max-retries";
    public static final String ExchangeNormal = BusinessPrefix + "-normal-x";
    public static final String QueueNormal = BusinessPrefix + "-normal-q";
    public static final String RoutingKey = "routingKey1";
    /**
     * 死信交换机
     */
    public static final String ExchangeDeadLetter = BusinessPrefix + "-dlx";
    /**
     * 死信队列
     */
    public static final String QueueDeadLetter = BusinessPrefix + "-dlq";

    @Resource
    AmqpTemplate rabbitTemplate;

    private AtomicInteger counter = new AtomicInteger();
    private AtomicInteger counterDeadLetter = new AtomicInteger();

    @RabbitListener(queues = QueueNormal)
    public void receiveMessage(MessageDTO messageDTO, Message message, Channel channel) throws Exception {
        log.info("Business Received " + messageDTO.toString());

        try {
            counter.incrementAndGet();
            boolean b = true;
            if(b) {
                throw new Exception("模拟发生业务异常进入死信");
            }

            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception ex) {
            Integer xMaxRetries = message.getMessageProperties().getHeader(HeaderXMaxRetries);
            log.info("x-max-retries={}", xMaxRetries);
            if(xMaxRetries == null || xMaxRetries < 1) {
                channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
            } else {
                channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            }
        }
    }

    public int getCount() {
        return this.counter.get();
    }

    @RabbitListener(queues = QueueDeadLetter)
    public void receiveMessageDeadLetter(Message message, Channel channel) throws Exception {
        Integer xMaxRetries = message.getMessageProperties().getHeader(HeaderXMaxRetries);
        if(xMaxRetries == null) {
            xMaxRetries = 0;
        }

        counterDeadLetter.incrementAndGet();
        message.getMessageProperties().setHeader(HeaderXMaxRetries, ++xMaxRetries);
        this.rabbitTemplate.convertAndSend(ExchangeNormal, RoutingKey, message, MessagePostProcessortVariable);

        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

    public int getCountDeadLetter() {
        return this.counterDeadLetter.get();
    }

    private final static MessagePostProcessor MessagePostProcessortVariable = message -> {
        message.getMessageProperties().setHeader("x-delay", 2000);
        return message;
    };

    // 不需要修改死信路由key，因为仅供内部使用
    private static final String DlqRoutingKey = "dlqRoutingKey1";

    @Bean
    public Queue normalQueue() {
        return QueueBuilder.nonDurable(QueueNormal)
                //声明绑定的死信交换机
                .deadLetterExchange(ExchangeDeadLetter)
                //声明死信路由key
                .deadLetterRoutingKey(DlqRoutingKey)
                .build();
    }

    // 配置默认的交互机
    @Bean
    public AbstractExchange normalExchange() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-delayed-type", "direct");
        // 参数二为类型：必须是x-delayed-message
        return new CustomExchange(ExchangeNormal, "x-delayed-message", false, false, args);
    }

    // 绑定队列到交换器
    @Bean
    public Binding businessBinding() {
        return BindingBuilder.bind(normalQueue()).to(normalExchange()).with(RoutingKey).noargs();
    }

    @Bean
    public Queue deadLetterQueue() {
        return QueueBuilder.nonDurable(QueueDeadLetter).build();
    }

    @Bean
    public AbstractExchange deadLetterExchange() {
        return new DirectExchange(ExchangeDeadLetter, false, false);
    }

    @Bean
    public Binding deadBinding() {
        return BindingBuilder.bind(deadLetterQueue()).to(deadLetterExchange()).with(DlqRoutingKey).noargs();
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
