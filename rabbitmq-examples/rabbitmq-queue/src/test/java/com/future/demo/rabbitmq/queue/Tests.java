package com.future.demo.rabbitmq.queue;

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

    @Test
    public void test() throws IOException, TimeoutException, InterruptedException {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost(Config.Host);
        connectionFactory.setUsername(Config.Username);
        connectionFactory.setPassword(Config.Password);

        Connection connection = connectionFactory.newConnection();
        Connection connectionConsumer = connectionFactory.newConnection();

        Channel channel = connection.createChannel();
        Channel channelConsumer = connectionConsumer.createChannel();

        // 临时队列
        String queuename = channelConsumer.queueDeclare().getQueue();

        int totalMessageProduce = 1000;
        List<String> listMessageConsume = new ArrayList<String>();
        CountDownLatch countDownLatch = new CountDownLatch(totalMessageProduce);
        channelConsumer.basicConsume(queuename, true, ((consumerTag, message) -> {
            String messageStr = new String(message.getBody(), StandardCharsets.UTF_8);
            listMessageConsume.add(messageStr);
            countDownLatch.countDown();
        }), consumerTag -> {});

        List<String> listMessageProduce = new ArrayList<String>();
        for(int i=0 ; i<totalMessageProduce; i++){
            String message = UUID.randomUUID().toString();
            listMessageProduce.add(message);
        }
        for(int i=0; i<totalMessageProduce; i++){
            String message = listMessageProduce.get(i);
            // 发送消息到默认交换机
            channel.basicPublish("", queuename, null, message.getBytes(StandardCharsets.UTF_8));
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
