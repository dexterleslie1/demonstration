package com.future.demo;

import com.future.common.http.ObjectResponse;
import com.future.common.http.ResponseUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class ApiController {

    @Resource
    AmqpTemplate amqpTemplate;

    @GetMapping("send")
    public ObjectResponse<String> send() {
        this.amqpTemplate.convertAndSend(Config.exchangeName, null, UUID.randomUUID().toString());
        return ResponseUtils.successObject("消息发送成功");
    }
}
