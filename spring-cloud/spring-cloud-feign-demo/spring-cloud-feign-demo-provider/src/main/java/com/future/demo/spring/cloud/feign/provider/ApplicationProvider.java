package com.future.demo.spring.cloud.feign.provider;

import com.future.common.exception.EnableFutureExceptionHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableFutureExceptionHandler
@EnableDiscoveryClient
public class ApplicationProvider {
    public static void main(String[] args) {
        SpringApplication.run(ApplicationProvider.class, args);
    }
}
