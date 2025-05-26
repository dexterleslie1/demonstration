package com.future.demo;

import com.future.demo.config.Config;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@SpringBootTest(classes = {ApplicationService.class})
@Slf4j
public class ApplicationTests {

    @Resource
    DefaultMQProducer producer;
    @Resource
    AtomicInteger counter;

    @Test
    public void test1() throws InterruptedException, MQBrokerException, RemotingException, MQClientException {
        for (int i = 0; i < Config.TotalMessageCount; i++) {
            // 创建消息实例，指定 topic、Tag和消息体
            Message msg = new Message("TestTopic", "TagA", ("Hello RocketMQ").getBytes());
            // 发送消息并获取发送结果
            SendResult sendResult = producer.send(msg);
            Assertions.assertEquals(SendStatus.SEND_OK, sendResult.getSendStatus());
        }

        TimeUnit.SECONDS.sleep(2);

        Assertions.assertEquals(Config.TotalMessageCount, counter.get());
    }
}
