package com.future.demo;

import com.future.common.exception.EnableFutureExceptionHandler;
import com.future.demo.feign.AccountClient;
import com.future.demo.feign.StorageClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFutureExceptionHandler
public class ApplicationOrder {
    public static void main(String[] args) {
        SpringApplication.run(ApplicationOrder.class, args);
    }
}
