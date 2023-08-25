package com.future.demo;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@DirtiesContext
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class}, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class ApplicationTests {

    @Resource
    private KafkaTemplate<String, String> kafkaTemplate;
    @Resource
    ConfigListeners configListeners;

    @Test
    public void test() throws Exception {
        final int count = 5;

        // 设置CountDownLatch
        this.configListeners.countDownLatch = new CountDownLatch(count);
        Assert.assertEquals(count, this.configListeners.countDownLatch.getCount());

        ExecutorService executorService = Executors.newCachedThreadPool();
        executorService.submit(() -> {
            for (int i = 0; i < count; i++) {
                String topic = "my-topic-1";
                String message = UUID.randomUUID().toString();
                kafkaTemplate.send(topic, message);
            }
        });

        executorService.shutdown();
        while (!executorService.awaitTermination(1, TimeUnit.SECONDS)) ;

        Assert.assertTrue(this.configListeners.countDownLatch.await(2, TimeUnit.SECONDS));
        Assert.assertEquals(0, this.configListeners.countDownLatch.getCount());
    }

}
