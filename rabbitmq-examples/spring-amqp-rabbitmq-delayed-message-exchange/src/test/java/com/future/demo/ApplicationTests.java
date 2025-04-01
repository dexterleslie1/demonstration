package com.future.demo;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class}, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class ApplicationTests {
    @Autowired
    private AmqpTemplate rabbitTemplate;
    @Autowired
    private ConfigDemoMq configDemoMq;

    @Test
    public void test() throws InterruptedException {
        // 清除之前的消息
        Thread.sleep(3100);
        this.configDemoMq.clear();

        int totalCount = 5;

        for (int i = 0; i < totalCount; i++) {
            this.rabbitTemplate.convertAndSend(ConfigDemoMq.ExchangeName, ConfigDemoMq.RoutingKey, "888", ConfigDemoMq.MessagePostProcessortVariable);
        }

        Thread.sleep(500);
        // 未收到delay消息
        Assert.assertEquals(0, configDemoMq.getCount());

        Thread.sleep(2600);
        // 收到所有delay消息
        Assert.assertEquals(totalCount, configDemoMq.getCount());

        // 最多重试一次
        Thread.sleep(5000);
        Assert.assertEquals(totalCount * 2, configDemoMq.getCount());
    }
}
