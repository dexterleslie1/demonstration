package com.future.demo;

import com.future.common.exception.EnableFutureExceptionHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableFutureExceptionHandler
@EnableEurekaClient
public class ApplicationAccount {
    public static void main(String[] args) {
        SpringApplication.run(ApplicationAccount.class, args);
    }
}
