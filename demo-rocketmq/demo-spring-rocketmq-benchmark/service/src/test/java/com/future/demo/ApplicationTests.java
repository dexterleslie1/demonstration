package com.future.demo;

import com.future.demo.config.Config;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.common.message.Message;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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
    public void test1() throws InterruptedException {
        // 并发发送消息以测试批量处理消息
        AtomicInteger iCounter = new AtomicInteger();
        ExecutorService threadPool = Executors.newCachedThreadPool();
        for (int i = 0; i < 8; i++) {
            threadPool.submit(() -> {
                try {
                    while (iCounter.getAndIncrement() < Config.TotalMessageCount) {
                        // 创建消息实例，指定 topic、Tag和消息体
                        Message msg = new Message("TestTopic", "TagA", ("Hello RocketMQ").getBytes());
                        // 发送消息并获取发送结果
                        SendResult sendResult = producer.send(msg);
                        Assertions.assertEquals(SendStatus.SEND_OK, sendResult.getSendStatus());
                    }
                } catch (Exception ex) {
                    log.error(ex.getMessage(), ex);
                }
            });
        }

        TimeUnit.SECONDS.sleep(3);

        Assertions.assertEquals(Config.TotalMessageCount, counter.get());
    }
}
