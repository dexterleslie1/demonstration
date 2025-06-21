package com.future.demo.controller;

import com.future.common.http.ObjectResponse;
import com.future.common.http.ResponseUtils;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import static com.future.demo.constant.Constant.TopicName;

@RestController
@RequestMapping("/api/v1")
public class ApiController {

    @Resource
    private KafkaTemplate<String, String> kafkaTemplate;

    @GetMapping("send")
    public ObjectResponse<String> send() throws ExecutionException, InterruptedException {
        String message = UUID.randomUUID().toString();
        kafkaTemplate.send(TopicName, message).get();
        return ResponseUtils.successObject("消息发送成功");
    }
}
