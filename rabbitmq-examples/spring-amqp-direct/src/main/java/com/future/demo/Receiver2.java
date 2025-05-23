package com.future.demo;

import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Dexterleslie.Chan
 */
@Component
@RabbitListener(bindings = @QueueBinding(
        value = @Queue(value = Config.QueueName, autoDelete = "true"),
        exchange = @Exchange(value = Config.ExchangeName, type = ExchangeTypes.DIRECT, autoDelete = "true"),
        key = Config.RoutingKey
))
public class Receiver2 {
    private static final Logger logger = LoggerFactory.getLogger(Receiver2.class);

    @Resource
    AtomicInteger counter;

    @RabbitHandler
    public void receiveMessage(String message,
                               Channel channel,
                               @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws Exception {
        logger.info("Received <" + message + ">");
        counter.incrementAndGet();
        channel.basicAck(deliveryTag, false);
    }

}
