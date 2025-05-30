package com.future.demo;

import org.junit.Before;
import org.junit.Test;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.util.StringUtils;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 *
 */
public class ApplicationTests {
    String host;
    String username;
    String password;

    @Before
    public void setup() {
        String host = System.getenv("host");
        String username = System.getenv("username");
        String password = System.getenv("password");

        this.host = host;
        this.username = username;
        this.password = password;

        if(!StringUtils.hasText(this.host)) {
            this.host = "localhost";
        }

        if(!StringUtils.hasText(this.username)) {
            this.username = "root";
        }

        if(!StringUtils.hasText(this.password)) {
            this.password = "123456";
        }
    }

    /**
     *
     */
    @Test
    public void test1() throws InterruptedException, TimeoutException {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory(host);
        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);

        RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory);
        String exchangename = "chat.message.push.exchange";
        DirectExchange exchange = new DirectExchange(exchangename, false, true);
        rabbitAdmin.declareExchange(exchange);

        Queue queue1 = new AnonymousQueue();
        rabbitAdmin.declareQueue(queue1);
        String userId1 = "111";
        Binding binding = BindingBuilder.bind(queue1).to(exchange).with(userId1);
        rabbitAdmin.declareBinding(binding);

        Queue queue2 = new AnonymousQueue();
        rabbitAdmin.declareQueue(queue2);
        String userId2 = "112";
        binding = BindingBuilder.bind(queue2).to(exchange).with(userId2);
        rabbitAdmin.declareBinding(binding);

        int totalMessages = 10;
        CountDownLatch countDownLatch1 = new CountDownLatch(totalMessages);
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory);
        container.addQueues(queue1);
        container.setAcknowledgeMode(AcknowledgeMode.AUTO);
        container.setConcurrentConsumers(100);
        MessageListener messageListener = new MessageListener() {
            @Override
            public void onMessage(Message message) {
                countDownLatch1.countDown();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    //
                }
            }
        };
        container.setMessageListener(messageListener);
        container.afterPropertiesSet();
        container.start();

        CountDownLatch countDownLatch2 = new CountDownLatch(totalMessages);
        container = new SimpleMessageListenerContainer(connectionFactory);
        container.addQueues(queue2);
        container.setAcknowledgeMode(AcknowledgeMode.AUTO);
        container.setConcurrentConsumers(100);
        messageListener = new MessageListener() {
            @Override
            public void onMessage(Message message) {
                countDownLatch2.countDown();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    //
                }
            }
        };
        container.setMessageListener(messageListener);
        container.afterPropertiesSet();
        container.start();

        RabbitTemplate amqpTemplate = new RabbitTemplate(connectionFactory);
        for(int i=0; i<totalMessages; i++) {
            amqpTemplate.convertAndSend(exchangename, userId1, "Hello world " + i);
        }
        for(int i=0; i<totalMessages; i++) {
            amqpTemplate.convertAndSend(exchangename, userId2, "Hello world " + i);
        }

        if(!countDownLatch1.await(2, TimeUnit.SECONDS)
            || !countDownLatch2.await(2, TimeUnit.SECONDS)){
            throw new TimeoutException();
        }

        connectionFactory.destroy();
    }
}
