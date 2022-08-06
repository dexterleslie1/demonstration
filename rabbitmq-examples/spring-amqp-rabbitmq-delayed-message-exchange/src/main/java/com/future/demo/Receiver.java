package com.future.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Dexterleslie.Chan
 */
@Component
public class Receiver {
    private static final Logger logger = LoggerFactory.getLogger(Receiver.class);

    private final AtomicInteger counter = new AtomicInteger();

    @RabbitListener(queues = Config.QueueName)
    public void receiveMessage(String message) {
        logger.info("Received <" + message + ">");
        counter.incrementAndGet();
    }

    public int getCount() {
        return this.counter.get();
    }
}
