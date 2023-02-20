package com.future.demo;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@RunWith(SpringRunner.class)
@SpringBootTest(classes={Application.class}, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class ApplicationTests {
    @Autowired
    private AmqpTemplate rabbitTemplate;
    @Autowired
    private BusinessReceiver businessReceiver;
    @Autowired
    private DeadReceiver deadReceiver;

    @Test
    public void test1() throws InterruptedException {
        int totalCount = 5;

        for(int i=0; i<totalCount; i++){
            MessageDTO messageDTO = new MessageDTO();
            messageDTO.setType("测试类型");
            this.rabbitTemplate.convertAndSend(Config.BusinessExchangeName, "routingKey1", messageDTO);
        }

        Thread.sleep(500);
        Assert.assertEquals(totalCount, this.businessReceiver.getCount());
        Assert.assertEquals(totalCount, this.deadReceiver.getCount());

        Thread.sleep(1000);
        Assert.assertEquals(totalCount, this.businessReceiver.getCount());
        Assert.assertEquals(totalCount, this.deadReceiver.getCount());

        Thread.sleep(1500);
        Assert.assertEquals(totalCount*2, this.businessReceiver.getCount());
        Assert.assertEquals(totalCount, this.deadReceiver.getCount());
    }
}
