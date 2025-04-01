package com.future.demo.rabbitmq.fanout;

import com.rabbitmq.client.*;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

public class Tests {

    @Test
    public void test() throws IOException, TimeoutException, InterruptedException {
        String exchangename = "demo-rabbitmq-exchange-fanout-testing";

        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost(Config.Host);
        connectionFactory.setUsername(Config.Username);
        connectionFactory.setPassword(Config.Password);

        Connection connection = connectionFactory.newConnection();
        Connection connectionConsumer = connectionFactory.newConnection();

        Channel channel = connection.createChannel();
        Channel channelConsumer = connectionConsumer.createChannel();

        // 定义fanout交换机
        channel.exchangeDeclare(exchangename, BuiltinExchangeType.FANOUT);

        // 定义两个匿名队列用于同时接收fanout交换机消息
        String queuename1 = channelConsumer.queueDeclare().getQueue();
        String queuename2 = channelConsumer.queueDeclare().getQueue();

        // 绑定匿名队列到fanout交换机
        // routingKey不起作用
        channelConsumer.queueBind(queuename1, exchangename, "no-effect1");
        channelConsumer.queueBind(queuename2, exchangename, "no-effect2");

        int totalMessageProduce = 5;

        List<String> consumer1List = new ArrayList<>();
        List<String> consumer2List = new ArrayList<>();
        List<String> finalConsumer1List = consumer1List;
        channelConsumer.basicConsume(queuename1, true, (consumerTag, message) -> {
            String bodyStr = new String(message.getBody(), StandardCharsets.UTF_8);
            finalConsumer1List.add(bodyStr);
        }, consumerTag -> {});
        List<String> finalConsumer2List = consumer2List;
        channelConsumer.basicConsume(queuename2, true, (consumerTag, message) -> {
            String bodyStr = new String(message.getBody(), StandardCharsets.UTF_8);
            finalConsumer2List.add(bodyStr);
        }, consumerTag -> {});

        List<String> messageList = new ArrayList<>();
        for(int i=0; i<totalMessageProduce; i++){
            String uuidStr = UUID.randomUUID().toString();
            messageList.add(uuidStr);
            // routingKey不起作用
            channel.basicPublish(exchangename, "no-effect3", null, uuidStr.getBytes(StandardCharsets.UTF_8));
        }

        Thread.sleep(1000);

        connection.close();
        connectionConsumer.close();

        messageList = messageList.stream().sorted(String::compareTo).collect(Collectors.toList());
        consumer1List = consumer1List.stream().sorted(String::compareTo).collect(Collectors.toList());
        consumer2List = consumer2List.stream().sorted(String::compareTo).collect(Collectors.toList());
        Assert.assertArrayEquals(messageList.toArray(), consumer1List.toArray());
        Assert.assertArrayEquals(messageList.toArray(), consumer2List.toArray());
    }

    @Test
    public void test2() throws IOException, TimeoutException, InterruptedException {
        String exchangename = "demo-rabbitmq-exchange-fanout-testing";
        String queuename = "demo-rabbitmq-queue-fanout-testing";

        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost(Config.Host);
        connectionFactory.setUsername(Config.Username);
        connectionFactory.setPassword(Config.Password);

        Connection connection = connectionFactory.newConnection();
        Connection connectionConsumer = connectionFactory.newConnection();

        Channel channel = connection.createChannel();
        Channel channelConsumer = connectionConsumer.createChannel();

        // 定义fanout交换机
        channel.exchangeDeclare(exchangename, BuiltinExchangeType.FANOUT);

        channelConsumer.queueDeclare(queuename, false, false, true, null);

        // 绑定队列到fanout交换机
        // routingKey不起作用
        channelConsumer.queueBind(queuename, exchangename, "no-effect1");

        int totalMessageProduce = 5;

        List<String> consumer1List = new ArrayList<>();
        List<String> consumer2List = new ArrayList<>();
        List<String> finalConsumer1List = consumer1List;
        channelConsumer.basicConsume(queuename, true, (consumerTag, message) -> {
            String bodyStr = new String(message.getBody(), StandardCharsets.UTF_8);
            finalConsumer1List.add(bodyStr);
        }, consumerTag -> {});
        List<String> finalConsumer2List = consumer2List;
        channelConsumer.basicConsume(queuename, true, (consumerTag, message) -> {
            String bodyStr = new String(message.getBody(), StandardCharsets.UTF_8);
            finalConsumer2List.add(bodyStr);
        }, consumerTag -> {});

        List<String> messageList = new ArrayList<>();
        for(int i=0; i<totalMessageProduce; i++){
            String uuidStr = UUID.randomUUID().toString();
            messageList.add(uuidStr);
            // routingKey不起作用
            channel.basicPublish(exchangename, "no-effect3", null, uuidStr.getBytes(StandardCharsets.UTF_8));
        }

        Thread.sleep(1000);

        connection.close();
        connectionConsumer.close();

        // 一个队列两个消费者是竞争关系（一个消息只能够被一个消费者接收）
        messageList = messageList.stream().sorted(String::compareTo).collect(Collectors.toList());
        consumer1List.addAll(consumer2List);
        consumer1List = consumer1List.stream().sorted(String::compareTo).collect(Collectors.toList());
        Assert.assertArrayEquals(messageList.toArray(), consumer1List.toArray());
    }
}
