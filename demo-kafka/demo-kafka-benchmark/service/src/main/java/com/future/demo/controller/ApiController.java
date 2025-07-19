package com.future.demo.controller;

import com.future.common.http.ObjectResponse;
import com.future.common.http.ResponseUtils;
import com.future.demo.constant.Constant;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/v1")
public class ApiController {

    @Resource
    private KafkaTemplate<String, String> kafkaTemplate;

    @GetMapping("sendToTopic1")
    public ObjectResponse<String> sendToTopic1() throws ExecutionException, InterruptedException {
        String message = UUID.randomUUID().toString();
        kafkaTemplate.send(Constant.Topic1, message).get();
        return ResponseUtils.successObject("消息发送成功");
    }

    @GetMapping("sendToTopic2")
    public ObjectResponse<String> sendToTopic2() throws ExecutionException, InterruptedException {
        String message = UUID.randomUUID().toString();
        kafkaTemplate.send(Constant.Topic2, message).get();
        return ResponseUtils.successObject("消息发送成功");
    }
}
