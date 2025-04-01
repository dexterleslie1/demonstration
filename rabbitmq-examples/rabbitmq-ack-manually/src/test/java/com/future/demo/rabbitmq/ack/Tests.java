package com.future.demo.rabbitmq.ack;

import com.rabbitmq.client.*;
import org.awaitility.Awaitility;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

public class Tests {
    /**
     * 测试basicAck方法
     *
     * @throws IOException
     * @throws TimeoutException
     * @throws InterruptedException
     */
    @Test
    public void test_basicack() throws IOException, TimeoutException, InterruptedException {
        String queueName = "demo-rabbitmq-ack-manually-testing";

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
        // 手动应答
        boolean ack = false;
        AtomicInteger finalAtomicInteger1 = atomicInteger;
        channelConsumer.basicConsume(queueName, ack, ((consumerTag, message) -> {
            finalAtomicInteger1.incrementAndGet();
        }), consumerTag -> {});
        int totalMessage = 5;
        for(int i=0; i<totalMessage; i++){
            String message = UUID.randomUUID().toString();
            channel.basicPublish("", queueName, null, message.getBytes(StandardCharsets.UTF_8));
        }
        Awaitility.await().atMost(Duration.ofSeconds(5)).pollInterval(Duration.ofSeconds(1)).until(() -> finalAtomicInteger1.get() == totalMessage);
        // 再等待1秒查看消息处理数是否会大于totalMessage
        Thread.sleep(1000);
        // 不确认消息关闭连接，下次打开新连接将会继续接收到同一条消息
        connectionConsumer.close();
        Assert.assertEquals(totalMessage, atomicInteger.get());

        // 此次打开连接将会接收到上一条未确认消息
        atomicInteger = new AtomicInteger();
        connectionConsumer = connectionFactory.newConnection();
        channelConsumer = connectionConsumer.createChannel();
        AtomicInteger finalAtomicInteger = atomicInteger;
        Channel finalChannelConsumer = channelConsumer;
        channelConsumer.basicConsume(queueName, false, (consumerTag, message) -> {
            finalAtomicInteger.incrementAndGet();
            finalChannelConsumer.basicAck(message.getEnvelope().getDeliveryTag(), false);
        }, consumerTag -> {});
        Awaitility.await().atMost(Duration.ofSeconds(5)).pollInterval(Duration.ofSeconds(1)).until(() -> finalAtomicInteger1.get() == totalMessage);
        // 再等待1秒查看消息处理数是否会大于totalMessage
        Thread.sleep(1000);
        connectionConsumer.close();
        Assert.assertEquals(totalMessage, atomicInteger.get());

        // 此次打开连接不会再接收到任何消息
        atomicInteger = new AtomicInteger();
        connectionConsumer = connectionFactory.newConnection();
        channelConsumer = connectionConsumer.createChannel();
        Channel finalChannelConsumer1 = channelConsumer;
        channelConsumer.basicConsume(queueName, true, (consumerTag, message) -> {
            finalChannelConsumer1.basicAck(message.getEnvelope().getDeliveryTag(), false);
        }, consumerTag -> {});
        Thread.sleep(1000);
        connectionConsumer.close();
        Assert.assertEquals(0, atomicInteger.get());

        connection.close();
    }

    /**
     * 测试basicNack
     */
    @Test
    public void test_basicnack() throws IOException, TimeoutException, InterruptedException {
        String queueName = "demo-rabbitmq-ack-manually-testing";

        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost(Config.Host);
        connectionFactory.setUsername(Config.Username);
        connectionFactory.setPassword(Config.Password);

        Connection connection = connectionFactory.newConnection();
        Connection connectionConsumer = connectionFactory.newConnection();

        Channel channel = connection.createChannel();

        channel.queueDeclare(queueName, false, false, false, null);

        Channel channelConsumer = connectionConsumer.createChannel();
        // 手动应答
        boolean ack = false;
        Map<String, Integer> keyToCountMap = new HashMap<>();
        List<String> finishList = new ArrayList<>();
        channelConsumer.basicConsume(queueName, ack, ((consumerTag, message) -> {
            String key = new String(message.getBody(), StandardCharsets.UTF_8);
            if(!keyToCountMap.containsKey(key)) {
                keyToCountMap.put(key, 1);
                channelConsumer.basicNack(message.getEnvelope().getDeliveryTag(), false, true);
            } else {
                keyToCountMap.put(key, keyToCountMap.get(key) + 1);
                channelConsumer.basicAck(message.getEnvelope().getDeliveryTag(), false);
            }
            if(keyToCountMap.get(key) == 2) {
                finishList.add(key);
            }
        }), consumerTag -> {});
        int totalMessage = 5;
        for(int i=0; i<totalMessage; i++){
            String message = UUID.randomUUID().toString();
            channel.basicPublish("", queueName, null, message.getBytes(StandardCharsets.UTF_8));
        }
        Awaitility.await().atMost(Duration.ofSeconds(5)).pollInterval(Duration.ofSeconds(1)).until(() -> finishList.size() == totalMessage);

        connectionConsumer.close();
        connection.close();

        keyToCountMap.forEach((key, value) -> Assert.assertEquals(2, value.intValue()));
    }

    /**
     * 测试basicReject
     */
    @Test
    public void test_basicreject() throws IOException, TimeoutException, InterruptedException {
        // 场景： requeue
        String queueName = "demo-rabbitmq-ack-manually-testing";

        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost(Config.Host);
        connectionFactory.setUsername(Config.Username);
        connectionFactory.setPassword(Config.Password);

        Connection connection = connectionFactory.newConnection();
        Connection connectionConsumer = connectionFactory.newConnection();

        Channel channel = connection.createChannel();

        channel.queueDeclare(queueName, false, false, false, null);

        Channel channelConsumer = connectionConsumer.createChannel();
        // 手动应答
        boolean ack = false;
        Map<String, Integer> keyToCountMap = new HashMap<>();
        List<String> finishList = new ArrayList<>();
        Map<String, Integer> finalKeyToCountMap1 = keyToCountMap;
        Channel finalChannelConsumer = channelConsumer;
        channelConsumer.basicConsume(queueName, ack, ((consumerTag, message) -> {
            String key = new String(message.getBody(), StandardCharsets.UTF_8);
            if(!finalKeyToCountMap1.containsKey(key)) {
                finalKeyToCountMap1.put(key, 1);
                // 消息被重新入队列，能够处理2次
                finalChannelConsumer.basicReject(message.getEnvelope().getDeliveryTag(), true);
            } else {
                finalKeyToCountMap1.put(key, finalKeyToCountMap1.get(key) + 1);
                finalChannelConsumer.basicAck(message.getEnvelope().getDeliveryTag(), false);
            }
            if(finalKeyToCountMap1.get(key) == 2) {
                finishList.add(key);
            }
        }), consumerTag -> {});
        int totalMessage = 5;
        for(int i=0; i<totalMessage; i++){
            String message = UUID.randomUUID().toString();
            channel.basicPublish("", queueName, null, message.getBytes(StandardCharsets.UTF_8));
        }
        Awaitility.await().atMost(Duration.ofSeconds(5)).pollInterval(Duration.ofSeconds(1)).until(() -> finishList.size() == totalMessage);

        connectionConsumer.close();
        connection.close();

        keyToCountMap.forEach((key, value) -> Assert.assertEquals(2, value.intValue()));

        // 场景： 不requeue
        connection = connectionFactory.newConnection();
        connectionConsumer = connectionFactory.newConnection();

        channel = connection.createChannel();

        channel.queueDeclare(queueName, false, false, false, null);

        channelConsumer = connectionConsumer.createChannel();
        // 手动应答
        ack = false;
        keyToCountMap = new HashMap<>();
        Map<String, Integer> finalKeyToCountMap = keyToCountMap;
        Channel finalChannelConsumer1 = channelConsumer;
        channelConsumer.basicConsume(queueName, ack, ((consumerTag, message) -> {
            String key = new String(message.getBody(), StandardCharsets.UTF_8);
            if(!finalKeyToCountMap.containsKey(key)) {
                finalKeyToCountMap.put(key, 0);
            }
            finalKeyToCountMap.put(key, finalKeyToCountMap.get(key) + 1);
            // 消息不会被重新入队列，所有只能够处理一次
            finalChannelConsumer1.basicReject(message.getEnvelope().getDeliveryTag(), false);
        }), consumerTag -> {});
        for(int i=0; i<totalMessage; i++){
            String message = UUID.randomUUID().toString();
            channel.basicPublish("", queueName, null, message.getBytes(StandardCharsets.UTF_8));
        }

        Thread.sleep(2000);

        connectionConsumer.close();
        connection.close();

        keyToCountMap.forEach((key, value) -> Assert.assertEquals(1, value.intValue()));
    }
}
