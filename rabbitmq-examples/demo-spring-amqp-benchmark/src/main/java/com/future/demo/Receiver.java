package com.future.demo;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class Receiver {
    @Resource
    AtomicInteger counter;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = Config.queueName, durable = "true", autoDelete = "false"),
            exchange = @Exchange(value = Config.exchangeName, type = ExchangeTypes.DIRECT, durable = "true", autoDelete = "false")
    ))
    public void receiveMessage(List<Message<String>> messageList, Channel channel) throws Exception {
        for (Message<String> message : messageList) {
            /*logger.info("Received <" + message.getPayload() + ">");*/
            Long deliveryTag = (Long) message.getHeaders().get(AmqpHeaders.DELIVERY_TAG);
            channel.basicAck(deliveryTag, false);

            int count = counter.incrementAndGet();
            if ((count % (10000 * 10)) == 0) {
                log.info("已经消费{}个消息", count);
            }
        }
    }
}
