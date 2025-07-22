package com.future.demo.config;

import com.future.demo.constant.Constant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;

import java.util.ArrayList;
import java.util.List;

@Configuration
@Slf4j
public class ConfigKafkaListener {

    public List<String> List = new ArrayList<>();

    @KafkaListener(topics = Constant.TopicTestSendPerf,
            groupId = "group-topicBatchSend1",
            containerFactory = "defaultKafkaListenerContainerFactory")
    public void receiveMessageFromTopicBatchSend1(List<String> messages) {
        for (String message : messages) {
            List.add(message + ":" + Constant.TopicTestSendPerf);
        }
    }
}
