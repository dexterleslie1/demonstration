package com.future.demo;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes={Application.class}, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class ApplicationTests {
    @Autowired
    private AmqpTemplate rabbitTemplate;
    @Autowired
    private ConfigDemoMq configDemoMq;

    @Test
    public void test1() throws InterruptedException {
        int totalCount = 5;

        for(int i=0; i<totalCount; i++){
            MessageDTO messageDTO = new MessageDTO();
            messageDTO.setType("测试类型");
            this.rabbitTemplate.convertAndSend(ConfigDemoMq.ExchangeNormal, ConfigDemoMq.RoutingKey, messageDTO);
        }

        Thread.sleep(500);
        Assert.assertEquals(totalCount, this.configDemoMq.getCount());
        Assert.assertEquals(totalCount, this.configDemoMq.getCountDeadLetter());

        Thread.sleep(1000);
        Assert.assertEquals(totalCount, this.configDemoMq.getCount());
        Assert.assertEquals(totalCount, this.configDemoMq.getCountDeadLetter());

        Thread.sleep(1500);
        Assert.assertEquals(totalCount*2, this.configDemoMq.getCount());
        Assert.assertEquals(totalCount, this.configDemoMq.getCountDeadLetter());

        Thread.sleep(3000);
        Assert.assertEquals(totalCount*2, this.configDemoMq.getCount());
        Assert.assertEquals(totalCount, this.configDemoMq.getCountDeadLetter());
    }
}
