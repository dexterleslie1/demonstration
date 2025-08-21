package com.future.demo;

import com.future.common.exception.EnableFutureExceptionHandler;
import com.future.count.EnableFutureCount;
import com.future.random.id.picker.EnableFutureRandomIdPicker;
import com.tencent.devops.leaf.plugin.annotation.EnableLeafServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
// 使 OrderService proxy = (OrderService) AopContext.currentProxy(); 生效
@EnableAspectJAutoProxy(exposeProxy = true)
@EnableFutureExceptionHandler
@EnableLeafServer
@EnableFutureRandomIdPicker
@EnableFutureCount
public class ApplicationService {
    public static void main(String[] args) {
        SpringApplication.run(ApplicationService.class, args);
    }
}
