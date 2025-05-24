package com.future.demo;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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
    CountDownLatch countDownLatch;
    @Resource
    AtomicInteger counter;
    @Resource
    AtomicInteger batchCounter;

    @Test
    public void test1() throws InterruptedException {
        AtomicInteger counterInternal = new AtomicInteger();
        ExecutorService threadPool = Executors.newCachedThreadPool();
        for (int i = 0; i < 8; i++) {
            threadPool.submit(() -> {
                int count;
                while ((count = counterInternal.getAndIncrement()) < Config.TotalMessageCount) {
                    amqpTemplate.convertAndSend(Config.exchangeName, null, "Hello from RabbitMQ!" + count);
                }
            });
        }
        threadPool.shutdown();
        while (threadPool.awaitTermination(10, TimeUnit.MILLISECONDS)) ;

        Assertions.assertTrue(countDownLatch.await(60, TimeUnit.SECONDS));

        Assertions.assertEquals(Config.TotalMessageCount, counter.get());
        log.info("批量回调共{}次", batchCounter.get());
    }
}
