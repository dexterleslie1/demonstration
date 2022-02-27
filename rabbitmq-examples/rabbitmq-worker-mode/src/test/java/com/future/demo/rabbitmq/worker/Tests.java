package com.future.demo.rabbitmq.worker;

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
    public void test() throws IOException, TimeoutException, InterruptedException {
        String queueName = "demo-rabbitmq-worker-mode";

        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost(Config.Host);
        connectionFactory.setUsername(Config.Username);
        connectionFactory.setPassword(Config.Password);

        Connection connection = connectionFactory.newConnection();
        Connection connectionConsumer = connectionFactory.newConnection();

        Channel channel = connection.createChannel();

        channel.queueDeclare(queueName, false, false, false, null);

        // 创建两个消费worker，论询方式消费消息，消息被公平分发到每个worker中
        AtomicInteger atomicInteger = new AtomicInteger();
        AtomicInteger atomicInteger1 = new AtomicInteger();

        Channel channelConsumer = connectionConsumer.createChannel();
        channelConsumer.basicConsume(queueName, true, ((consumerTag, message) -> {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                //
            }
            atomicInteger.incrementAndGet();
        }), consumerTag -> {});
        channelConsumer.basicConsume(queueName, true, ((consumerTag, message) -> {
            atomicInteger1.incrementAndGet();
        }), consumerTag -> {});

        int totalMessage = 100;
        for(int i=0; i<totalMessage; i++){
            String message = UUID.randomUUID().toString();
            channel.basicPublish("", queueName, null, message.getBytes(StandardCharsets.UTF_8));
        }

        Awaitility.await().atMost(Duration.ofSeconds(6)).pollInterval(Duration.ofSeconds(1)).until(() -> atomicInteger.get() == totalMessage/2);
        Awaitility.await().atMost(Duration.ofSeconds(6)).pollInterval(Duration.ofSeconds(1)).until(() -> atomicInteger1.get() == totalMessage/2);

        // 再等待1秒，检查是否有可能其中的worker线程处理数会超过totalMessage/2条消息
        Thread.sleep(1000);

        connection.close();
        connectionConsumer.close();

        Assert.assertEquals(totalMessage/2, atomicInteger.get());
        Assert.assertEquals(totalMessage/2, atomicInteger1.get());
    }
}
