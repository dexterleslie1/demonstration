package com.future.demo;

import org.springframework.stereotype.Service;

@Service
public class BusinessService {

    public String processMessage(String message) {
        // 这里可以实现复杂的业务逻辑，比如操作数据库、调用其他服务等
        return "处理后的: " + message.toUpperCase();
    }
}