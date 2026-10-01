package com.future.demo;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Dexterleslie.Chan
 */
@SpringBootTest(classes = {Application.class})
public class ApplicationTests {
    @Autowired
    private AmqpTemplate amqpTemplate = null;
    @Resource
    AtomicInteger counter;

    @Test
    public void test1() throws InterruptedException {
        int totalMessageCount = 2;
        for (int i = 0; i < totalMessageCount; i++) {
            amqpTemplate.convertAndSend(Config.ExchangeName, Config.RoutingKey, "Hello from RabbitMQ!" + i);
        }

        TimeUnit.MILLISECONDS.sleep(1000);

        Assertions.assertEquals(totalMessageCount, counter.get());
    }
}
