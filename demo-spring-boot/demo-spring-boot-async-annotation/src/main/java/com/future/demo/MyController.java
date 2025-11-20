package com.future.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MyController {

    @Autowired
    private EmailService emailService;

    @GetMapping("/send")
    public String sendMail() throws Exception {
        // 这个方法会立即返回，而不会等待邮件发送完成
        emailService.sendEmail("user@example.com", "主题", "内容");
        System.out.println("请求已处理，邮件发送任务已提交。线程: " + Thread.currentThread().getName());
        return "邮件正在后台发送中...";
    }
}
