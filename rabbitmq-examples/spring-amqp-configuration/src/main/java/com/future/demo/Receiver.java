package com.future.demo;

import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Dexterleslie.Chan
 */
@Component
public class Receiver {
    private static final Logger logger = LoggerFactory.getLogger(Receiver.class);

    @Resource
    AtomicInteger counter;
    @Resource
    AtomicInteger batchCounter;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = Config.queueName, autoDelete = "true"),
            exchange = @Exchange(value = Config.exchangeName, type = ExchangeTypes.FANOUT, autoDelete = "true")
    ))
    public void receiveMessage(List<Message<String>> messageList, Channel channel) throws Exception {
        for (Message<String> message : messageList) {
            logger.info("Received <" + message.getPayload() + ">");
            Long deliveryTag = (Long) message.getHeaders().get(AmqpHeaders.DELIVERY_TAG);
            channel.basicAck(deliveryTag, false);
            counter.incrementAndGet();
        }

        batchCounter.incrementAndGet();
    }

}
