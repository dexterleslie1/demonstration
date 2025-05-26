package com.future.demo.controller;

import com.future.common.http.ObjectResponse;
import com.future.common.http.ResponseUtils;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class ApiController {

    @Resource
    DefaultMQProducer producer;

    @GetMapping("send")
    public ObjectResponse<String> send() throws MQBrokerException, RemotingException, InterruptedException, MQClientException {
        Message msg = new Message("TestTopic", "TagA", UUID.randomUUID().toString().getBytes());
        this.producer.send(msg);
        return ResponseUtils.successObject("消息发送成功");
    }
}
