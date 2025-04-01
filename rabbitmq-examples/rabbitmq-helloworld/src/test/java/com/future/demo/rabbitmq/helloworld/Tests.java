package com.future.demo.rabbitmq.helloworld;

import com.rabbitmq.client.*;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class Tests {
    /**
     * 演示使用amqp-client连接rabbitmq服务器
     *
     * @throws IOException
     * @throws TimeoutException
     * @throws InterruptedException
     */
    @Test
    public void test() throws IOException, TimeoutException, InterruptedException {
        String queueName = "rabbitmq-examples-tutorial-helloworld";

        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost(Config.Host);
        connectionFactory.setUsername(Config.Username);
        connectionFactory.setPassword(Config.Password);

        Connection connection = connectionFactory.newConnection();
        Connection connectionConsumer = connectionFactory.newConnection();

        Channel channel = connection.createChannel();

        channel.queueDeclare(queueName, false, false, false, null);

        int totalMessageProduce = 1000;
        List<String> listMessageConsume = Collections.synchronizedList(new ArrayList<>());
        CountDownLatch countDownLatch = new CountDownLatch(totalMessageProduce);
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
            listMessageConsume.add(message);
            countDownLatch.countDown();
        };

        Channel channelConsumer = connectionConsumer.createChannel();
        channelConsumer.basicConsume(queueName, true, deliverCallback, consumerTag -> {});

        List<String> listMessageProduce = new ArrayList<>();
        for(int i=0 ; i<totalMessageProduce; i++){
            String message = UUID.randomUUID().toString();
            listMessageProduce.add(message);
        }
        for(int i=0; i<totalMessageProduce; i++){
            String message = listMessageProduce.get(i);
            // 发送消息到默认交换机
            channel.basicPublish("", queueName, null, message.getBytes(StandardCharsets.UTF_8));
        }

        if(!countDownLatch.await(5000, TimeUnit.MILLISECONDS)){
            throw new TimeoutException();
        }

        connection.close();
        connectionConsumer.close();

        Collections.sort(listMessageProduce);
        Collections.sort(listMessageConsume);
        Assert.assertArrayEquals(listMessageProduce.toArray(new String[]{}), listMessageConsume.toArray(new String[]{}));
    }
}
