package com.future.demo.rabbitmq.durable;

import com.rabbitmq.client.*;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

public class Tests {
    /**
     * 队列持久化
     * 运行测试后，会分别创建一个非持久化和持久化队列，重启rabbitmq后非持久化队列消失，持久化队列依旧存在
     *
     * @throws IOException
     * @throws TimeoutException
     */
    @Test
    public void test_queue_durable() throws IOException, TimeoutException {
        String queuenameNonedurable = "demo-rabbitmq-durable-nonedurable-testing";
        String queuenameDurable = "demo-rabbitmq-durable-dodurable-testing";

        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost(Config.Host);
        connectionFactory.setUsername(Config.Username);
        connectionFactory.setPassword(Config.Password);

        Connection connection = connectionFactory.newConnection();

        Channel channel = connection.createChannel();

        // 定义非持久化队列
        channel.queueDeclare(queuenameNonedurable, false, false, false, null);
        // 定义持久化队列
        channel.queueDeclare(queuenameDurable, true, false, false, null);

        connection.close();
    }

    /**
     * 消息持久化
     * 运行测试后，往持久化队列发送持久化和非持久化消息，重启rabbitmq后非持久化消息丢失，持久化消息依旧存在
     *
     * @throws IOException
     * @throws TimeoutException
     */
    @Test
    public void test_message_durable() throws IOException, TimeoutException {
        String queuenameDurable = "demo-rabbitmq-durable-dodurable-testing";

        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost(Config.Host);
        connectionFactory.setUsername(Config.Username);
        connectionFactory.setPassword(Config.Password);

        Connection connection = connectionFactory.newConnection();

        Channel channel = connection.createChannel();

        // 定义持久化队列
        channel.queueDeclare(queuenameDurable, true, false, false, null);

        // 发送3条非持久化消息
        for(int i=0; i<3; i++) {
            channel.basicPublish("", queuenameDurable, null, UUID.randomUUID().toString().getBytes(StandardCharsets.UTF_8));
        }
        // 发送3条持久化消息
        for(int i=0; i<3; i++) {
            channel.basicPublish("", queuenameDurable, MessageProperties.PERSISTENT_TEXT_PLAIN, UUID.randomUUID().toString().getBytes(StandardCharsets.UTF_8));
        }

        connection.close();
    }
}
