package com.future.demo.rabbitmq.qos;

import com.rabbitmq.client.*;
import org.awaitility.Awaitility;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

public class Tests {
    /**
     * 程序在没有消费者订阅队列前一次发送100个消息到消息队列，
     * 发送完毕后注册两个消费者qos分别为30%和70%比例分配100个消息到两个消费中
     * @throws IOException
     * @throws TimeoutException
     * @throws InterruptedException
     */
    @Test
    public void test() throws IOException, TimeoutException, InterruptedException {
        String queueName = "demo-rabbitmq-qos-testing";

        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost(Config.Host);
        connectionFactory.setUsername(Config.Username);
        connectionFactory.setPassword(Config.Password);

        Connection connection = connectionFactory.newConnection();
        Connection connectionWorker1 = connectionFactory.newConnection();
        Connection connectionWorker2 = connectionFactory.newConnection();

        int total = 100;
        double percentageWorker1 = 0.3;
        int countDownWorker1 = (int)(total*percentageWorker1);
        int countDownWorker2 = (int)(total*(1-percentageWorker1));
        AtomicInteger atomicIntegerWorker1 = new AtomicInteger();
        AtomicInteger atomicIntegerWorker2 = new AtomicInteger();
        List<Delivery> deliveryList1 = new ArrayList<>();
        List<Delivery> deliveryList2 = new ArrayList<>();

        Channel channel = connection.createChannel();
        channel.queueDeclare(queueName, false, false, true, null);
        for(int i=0; i<total; i++) {
            String message = UUID.randomUUID().toString();
            channel.basicPublish("", queueName, null, message.getBytes(StandardCharsets.UTF_8));
        }

        Channel channelWorker1 = connectionWorker1.createChannel();
        channelWorker1.basicQos(countDownWorker1, false);
        channelWorker1.basicConsume(queueName, false, (consumerTag, message) -> {
            atomicIntegerWorker1.incrementAndGet();
            deliveryList1.add(message);
        }, consumerTag -> {});
        Channel channelWorker2 = connectionWorker2.createChannel();
        channelWorker2.basicQos(countDownWorker2, false);
        channelWorker2.basicConsume(queueName, false, (consumerTag, message) -> {
            atomicIntegerWorker2.incrementAndGet();
            deliveryList2.add(message);
        }, consumerTag -> {});

        Awaitility.await().atMost(Duration.ofSeconds(5)).pollInterval(Duration.ofSeconds(1)).until(() -> deliveryList1.size() + deliveryList2.size() == total);

        deliveryList1.forEach(delivery -> {
            try {
                channelWorker1.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        deliveryList2.forEach(delivery -> {
            try {
                channelWorker2.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        connection.close();
        connectionWorker1.close();
        connectionWorker2.close();

        Assert.assertEquals(total, atomicIntegerWorker1.get()+atomicIntegerWorker2.get());
        Assert.assertEquals(countDownWorker1, atomicIntegerWorker1.get());
        Assert.assertEquals(countDownWorker2, atomicIntegerWorker2.get());
    }
}
