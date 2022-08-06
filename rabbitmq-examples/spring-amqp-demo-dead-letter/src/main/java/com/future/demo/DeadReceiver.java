package com.future.demo;

import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class DeadReceiver {
    private static final Logger logger = LoggerFactory.getLogger(DeadReceiver.class);

    @Autowired
    private AmqpTemplate rabbitTemplate;

    private AtomicInteger counter = new AtomicInteger();

    @RabbitListener(queues = Config.DEAD_QUEUE_NAME)
    public void receiveMessage(Message message, Channel channel) throws Exception {
        String msg = new String(message.getBody());
        logger.info("Dead Received <" + msg + ">");
        counter.incrementAndGet();
//        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);

        this.rabbitTemplate.convertAndSend(Config.BusinessExchangeName, "routingKey1", "999", MessagePostProcessortVariable);
    }

    public int getCount() {
        return this.counter.get();
    }

    private final static MessagePostProcessor MessagePostProcessortVariable = message -> {
        message.getMessageProperties().setHeader("x-delay", 2000);
        return message;
    };
}
