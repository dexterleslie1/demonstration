package com.future.demo.rabbitmq.direct;

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
    /**
     * 测试两个匿名队列routingKey都为""
     *
     * @throws IOException
     * @throws TimeoutException
     * @throws InterruptedException
     */
    @Test
    public void test() throws IOException, TimeoutException, InterruptedException {
        String exchangename = "demo-rabbitmq-exchange-direct-testing";

        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost(Config.Host);
        connectionFactory.setUsername(Config.Username);
        connectionFactory.setPassword(Config.Password);

        Connection connection = connectionFactory.newConnection();
        Connection connectionConsumer = connectionFactory.newConnection();

        Channel channel = connection.createChannel();
        Channel channelConsumer = connectionConsumer.createChannel();

        // 定义交换机
        channel.exchangeDeclare(exchangename, BuiltinExchangeType.DIRECT);

        // 定义两个匿名队列用于同时接收交换机消息
        String queuename1 = channelConsumer.queueDeclare().getQueue();
        String queuename2 = channelConsumer.queueDeclare().getQueue();

        // 绑定匿名队列到交换机
        channelConsumer.queueBind(queuename1, exchangename, "");
        channelConsumer.queueBind(queuename2, exchangename, "");

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
            channel.basicPublish(exchangename, "", null, uuidStr.getBytes(StandardCharsets.UTF_8));
        }

        Thread.sleep(1000);

        connection.close();
        connectionConsumer.close();

        // 因为routingKey都为“”，所以两个队列能够接收到一样的消息
        messageList = messageList.stream().sorted(String::compareTo).collect(Collectors.toList());
        consumer1List = consumer1List.stream().sorted(String::compareTo).collect(Collectors.toList());
        consumer2List = consumer2List.stream().sorted(String::compareTo).collect(Collectors.toList());
        Assert.assertArrayEquals(messageList.toArray(), consumer1List.toArray());
        Assert.assertArrayEquals(messageList.toArray(), consumer2List.toArray());
    }

    /**
     * 测试两个匿名队列routingKey都为"1"
     *
     * @throws IOException
     * @throws TimeoutException
     * @throws InterruptedException
     */
    @Test
    public void test1() throws IOException, TimeoutException, InterruptedException {
        String exchangename = "demo-rabbitmq-exchange-direct-testing";

        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost(Config.Host);
        connectionFactory.setUsername(Config.Username);
        connectionFactory.setPassword(Config.Password);

        Connection connection = connectionFactory.newConnection();
        Connection connectionConsumer = connectionFactory.newConnection();

        Channel channel = connection.createChannel();
        Channel channelConsumer = connectionConsumer.createChannel();

        // 定义交换机
        channel.exchangeDeclare(exchangename, BuiltinExchangeType.DIRECT);

        // 定义两个匿名队列用于同时接收交换机消息
        String queuename1 = channelConsumer.queueDeclare().getQueue();
        String queuename2 = channelConsumer.queueDeclare().getQueue();

        // 绑定匿名队列到交换机
        channelConsumer.queueBind(queuename1, exchangename, "1");
        channelConsumer.queueBind(queuename2, exchangename, "1");

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
            channel.basicPublish(exchangename, "1", null, uuidStr.getBytes(StandardCharsets.UTF_8));
        }

        Thread.sleep(1000);

        connection.close();
        connectionConsumer.close();

        // 因为routingKey都为“”，所以两个队列能够接收到一样的消息
        messageList = messageList.stream().sorted(String::compareTo).collect(Collectors.toList());
        consumer1List = consumer1List.stream().sorted(String::compareTo).collect(Collectors.toList());
        consumer2List = consumer2List.stream().sorted(String::compareTo).collect(Collectors.toList());
        Assert.assertArrayEquals(messageList.toArray(), consumer1List.toArray());
        Assert.assertArrayEquals(messageList.toArray(), consumer2List.toArray());
    }

    /**
     * 测试两个匿名队列routingKey分别为“1”、“2”
     *
     * @throws IOException
     * @throws TimeoutException
     * @throws InterruptedException
     */
    @Test
    public void test2() throws IOException, TimeoutException, InterruptedException {
        String exchangename = "demo-rabbitmq-exchange-direct-testing";

        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost(Config.Host);
        connectionFactory.setUsername(Config.Username);
        connectionFactory.setPassword(Config.Password);

        Connection connection = connectionFactory.newConnection();
        Connection connectionConsumer = connectionFactory.newConnection();

        Channel channel = connection.createChannel();
        Channel channelConsumer = connectionConsumer.createChannel();

        // 定义交换机
        channel.exchangeDeclare(exchangename, BuiltinExchangeType.DIRECT);

        // 定义两个匿名队列用于同时接收交换机消息
        String queuename1 = channelConsumer.queueDeclare().getQueue();
        String queuename2 = channelConsumer.queueDeclare().getQueue();

        // 绑定匿名队列到交换机
        channelConsumer.queueBind(queuename1, exchangename, "1");
        channelConsumer.queueBind(queuename2, exchangename, "2");

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

        List<String> message1List = new ArrayList<>();
        for(int i=0; i<totalMessageProduce; i++){
            String uuidStr = UUID.randomUUID().toString();
            message1List.add(uuidStr);
            channel.basicPublish(exchangename, "1", null, uuidStr.getBytes(StandardCharsets.UTF_8));
        }
        List<String> message2List = new ArrayList<>();
        for(int i=0; i<totalMessageProduce; i++){
            String uuidStr = UUID.randomUUID().toString();
            message2List.add(uuidStr);
            channel.basicPublish(exchangename, "2", null, uuidStr.getBytes(StandardCharsets.UTF_8));
        }

        Thread.sleep(1000);

        connection.close();
        connectionConsumer.close();

        // 因为routingKey都为“”，所以两个队列能够接收到一样的消息
        message1List = message1List.stream().sorted(String::compareTo).collect(Collectors.toList());
        message2List = message2List.stream().sorted(String::compareTo).collect(Collectors.toList());
        consumer1List = consumer1List.stream().sorted(String::compareTo).collect(Collectors.toList());
        consumer2List = consumer2List.stream().sorted(String::compareTo).collect(Collectors.toList());
        Assert.assertArrayEquals(message1List.toArray(), consumer1List.toArray());
        Assert.assertArrayEquals(message2List.toArray(), consumer2List.toArray());
    }

    /**
     * 测试同一个队列两个消费者
     *
     * @throws IOException
     * @throws TimeoutException
     * @throws InterruptedException
     */
    @Test
    public void test3() throws IOException, TimeoutException, InterruptedException {
        String exchangename = "demo-rabbitmq-exchange-direct-testing";
        String queuename = "demo-rabbitmq-queue-direct-testing";

        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost(Config.Host);
        connectionFactory.setUsername(Config.Username);
        connectionFactory.setPassword(Config.Password);

        Connection connection = connectionFactory.newConnection();
        Connection connectionConsumer = connectionFactory.newConnection();

        Channel channel = connection.createChannel();
        Channel channelConsumer = connectionConsumer.createChannel();

        // 定义交换机
        channel.exchangeDeclare(exchangename, BuiltinExchangeType.DIRECT);

        channelConsumer.queueDeclare(queuename, false, false, true, null);

        channelConsumer.queueBind(queuename, exchangename, "1");

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
            channel.basicPublish(exchangename, "1", null, uuidStr.getBytes(StandardCharsets.UTF_8));
        }

        Thread.sleep(1000);

        connection.close();
        connectionConsumer.close();

        // 同一个队列两个消费者，一个消息只能被一个消费者处理
        messageList = messageList.stream().sorted(String::compareTo).collect(Collectors.toList());
        consumer1List.addAll(consumer2List);
        consumer1List = consumer1List.stream().sorted(String::compareTo).collect(Collectors.toList());
        Assert.assertArrayEquals(messageList.toArray(), consumer1List.toArray());
    }
}
