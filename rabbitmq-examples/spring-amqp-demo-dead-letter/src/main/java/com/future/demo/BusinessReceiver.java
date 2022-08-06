package com.future.demo;

import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Dexterleslie.Chan
 */
@Component
public class BusinessReceiver {
    private static final Logger logger = LoggerFactory.getLogger(BusinessReceiver.class);

    private AtomicInteger counter = new AtomicInteger();

    @RabbitListener(queues = Config.BusinessQueueName)
    public void receiveMessage(Message message, Channel channel) throws Exception {
        String msg = new String(message.getBody());
        logger.info("Business Received <" + msg + ">");
        counter.incrementAndGet();
        try {
            boolean b = true;
            if(b) {
                throw new Exception("模拟发生业务异常进入死信");
            }
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception ex) {
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
        }
    }

    public int getCount() {
        return this.counter.get();
    }
}
