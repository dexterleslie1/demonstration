package com.future.demo.rabbitmq.topic;

import com.rabbitmq.client.*;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

public class Tests {
    /**
     *
     * @throws IOException
     * @throws TimeoutException
     * @throws InterruptedException
     */
    @Test
    public void test() throws IOException, TimeoutException, InterruptedException {
        String exchangeName = "demo-rabbitmq-exchange-topic-testing";
        String routingKey1Prefix = "routingKey1.";
        String routingKey2Prefix = "routingKey2.";
        String routingKey1 = routingKey1Prefix + "*";
        String routingKey2 = routingKey2Prefix + "*";

        AtomicInteger atomicInteger1 = new AtomicInteger(0);
        AtomicInteger atomicInteger2 = new AtomicInteger(0);

        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost(Config.Host);
        connectionFactory.setUsername(Config.Username);
        connectionFactory.setPassword(Config.Password);

        Connection connectionExchange = connectionFactory.newConnection();
        Connection connectionQueue1 = connectionFactory.newConnection();
        Connection connectionQueue2 = connectionFactory.newConnection();

        Channel channelExchange = connectionExchange.createChannel();
        channelExchange.exchangeDeclare(exchangeName, BuiltinExchangeType.TOPIC);

        Channel channel = connectionQueue1.createChannel();
        String queueName1 = channel.queueDeclare().getQueue();
        channel.queueBind(queueName1, exchangeName, routingKey1);
        channel.basicConsume(queueName1, true, (consumerTag, message) -> {
            atomicInteger1.incrementAndGet();
        }, consumerTag -> {});

        channel = connectionQueue2.createChannel();
        String queueName2 = channel.queueDeclare().getQueue();
        channel.queueBind(queueName2, exchangeName, routingKey1);
        channel.queueBind(queueName2, exchangeName, routingKey2);
        channel.basicConsume(queueName2, true, (consumerTag, message) -> {
            atomicInteger2.incrementAndGet();
        }, consumerTag -> {});

        String message = UUID.randomUUID().toString();
        channelExchange.basicPublish(exchangeName, routingKey1Prefix + message, null, message.getBytes());
        message = UUID.randomUUID().toString();
        channelExchange.basicPublish(exchangeName, routingKey1Prefix + message, null, message.getBytes());

        message = UUID.randomUUID().toString();
        channelExchange.basicPublish(exchangeName, routingKey2Prefix + message, null, message.getBytes());
        message = UUID.randomUUID().toString();
        channelExchange.basicPublish(exchangeName, routingKey2Prefix + message, null, message.getBytes());
        message = UUID.randomUUID().toString();
        channelExchange.basicPublish(exchangeName, routingKey2Prefix + message, null, message.getBytes());

        Thread.sleep(1000);

        connectionExchange.close();
        connectionQueue1.close();
        connectionQueue2.close();

        Assert.assertEquals(2, atomicInteger1.get());
        Assert.assertEquals(5, atomicInteger2.get());
    }
}
