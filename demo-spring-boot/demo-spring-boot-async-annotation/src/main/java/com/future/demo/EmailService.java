package com.future.demo;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    // 这个send方法将会被异步执行
    @Async("myTaskExecutor")
    public void sendEmail(String to, String subject, String content) throws Exception {
        // 模拟耗时的邮件发送过程
        try {
            Thread.sleep(5000); // 休眠5秒，模拟耗时

            // 协助测试异常情况处理
            /*boolean b = true;
            if(b) {
                throw new Exception("测试异常");
            }*/

            System.out.println("邮件发送成功给: " + to + "，线程: " + Thread.currentThread().getName());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
