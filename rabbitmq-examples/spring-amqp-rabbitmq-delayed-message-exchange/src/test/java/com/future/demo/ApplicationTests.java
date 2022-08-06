package com.future.demo;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest(classes={Application.class}, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class ApplicationTests {
    final static long Delay = 3000;

    @Autowired
    private AmqpTemplate rabbitTemplate;
    @Autowired
    private Receiver receiver;

    @Test
    public void test1() throws InterruptedException {
        int totalCount = 5;

        for(int i=0; i<totalCount; i++){
            this.rabbitTemplate.convertAndSend(Config.ExchangeName, "routingKey1", "888", MessagePostProcessortVariable);
        }

        Thread.sleep(1000);

        // 未收到delay消息
        Assert.assertEquals(0, receiver.getCount());

        Thread.sleep(2500);
        // 收到所有delay消息
        Assert.assertEquals(totalCount, receiver.getCount());
    }

    private final static MessagePostProcessor MessagePostProcessortVariable = message -> {
        message.getMessageProperties().setHeader("x-delay", Delay);
        return message;
    };
}
