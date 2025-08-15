package com.future.demo;

import com.future.common.exception.EnableFutureExceptionHandler;
import com.future.random.id.picker.EnableFutureRandomIdPicker;
import com.tencent.devops.leaf.plugin.annotation.EnableLeafServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableFutureExceptionHandler
@EnableLeafServer
@EnableFutureRandomIdPicker
public class ApplicationService {
    public static void main(String[] args) {
        SpringApplication.run(ApplicationService.class, args);
    }
}
