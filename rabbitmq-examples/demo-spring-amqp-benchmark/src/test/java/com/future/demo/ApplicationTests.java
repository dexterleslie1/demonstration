package com.future.demo;

import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class ApplicationTests {
    @Autowired
    private AmqpTemplate amqpTemplate = null;
    @Resource
    AtomicInteger counter;

    @Test
    public void test1() throws InterruptedException {
        for (int i = 0; i < Config.TotalMessageCount; i++) {
            amqpTemplate.convertAndSend(Config.exchangeName, null, "Hello from RabbitMQ!" + i);
        }

        TimeUnit.SECONDS.sleep(2);

        Assertions.assertEquals(Config.TotalMessageCount, counter.get());
    }
}
