package com.future.demo;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;

@Component
public class ConfigListeners {

    public CountDownLatch countDownLatch;

    @KafkaListener(topics = "my-topic-1")
    public void receiveMessage(String message) {
        this.countDownLatch.countDown();
    }

}
