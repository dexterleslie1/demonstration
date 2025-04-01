package com.future.demo;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class}, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestPropertySource("classpath:application-test.properties")
public class PerformanceTests {
    @Autowired
    private AmqpTemplate rabbitTemplate;
    @Autowired
    private ConfigDemoMq configDemoMq;

    @Test
    public void test() throws InterruptedException {
        // 清除之前的消息
        Thread.sleep(3500);
        this.configDemoMq.clear();

        int totalCount = 1024 * 1024;

        ExecutorService executorService = Executors.newCachedThreadPool();
        for (int i = 0; i < 128; i++) {
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    int count = totalCount / 128;
                    for (int j = 0; j < count; j++) {
                        String uuid = UUID.randomUUID().toString();
                        rabbitTemplate.convertAndSend(ConfigDemoMq.ExchangeName, ConfigDemoMq.RoutingKey, uuid, ConfigDemoMq.MessagePostProcessortVariable);
                    }
                }
            });
        }

        executorService.shutdown();
        while (!executorService.awaitTermination(1, TimeUnit.SECONDS)) ;

        Thread.sleep(3500);
        Assert.assertEquals(totalCount, configDemoMq.getCount());

        Thread.sleep(3500);
        Assert.assertEquals(totalCount * 2, configDemoMq.getCount());
    }
}
