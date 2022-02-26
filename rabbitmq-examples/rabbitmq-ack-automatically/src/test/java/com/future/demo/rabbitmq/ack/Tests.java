package com.future.demo.rabbitmq.ack;

import com.rabbitmq.client.*;
import org.awaitility.Awaitility;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

public class Tests {
    @Test
    public void test() throws IOException, TimeoutException {
        String queueName = "demo-rabbitmq-ack-automatically-testing";

        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost(Config.Host);
        connectionFactory.setUsername(Config.Username);
        connectionFactory.setPassword(Config.Password);

        Connection connection = connectionFactory.newConnection();
        Connection connectionConsumer = connectionFactory.newConnection();

        Channel channel = connection.createChannel();

        channel.queueDeclare(queueName, false, false, false, null);

        Channel channelConsumer = connectionConsumer.createChannel();
        AtomicInteger atomicInteger = new AtomicInteger();
        // 自动应答
        boolean ack = true;
        channelConsumer.basicConsume(queueName, ack, ((consumerTag, message) -> {
            atomicInteger.incrementAndGet();
            // 测试模拟抛出异常情况
//            boolean b = true;
//            if(b) {
//                throw new RuntimeException("kkkk");
//            }
        }), consumerTag -> {});

        int totalMessage = 100;
        for(int i=0; i<totalMessage; i++){
            String message = UUID.randomUUID().toString();
            channel.basicPublish("", queueName, null, message.getBytes(StandardCharsets.UTF_8));
        }

        Awaitility.await().atMost(Duration.ofSeconds(30)).pollInterval(Duration.ofSeconds(1)).until(() -> atomicInteger.get() == totalMessage);

        connection.close();
        connectionConsumer.close();

        Assert.assertEquals(totalMessage, atomicInteger.get());
    }
}
