package com.future.demo.consumer;

import com.future.common.exception.EnableFutureExceptionHandler;
import com.future.demo.common.feign.BpkcMxbFeign;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(
        clients = {
                BpkcMxbFeign.class
        }
)
@EnableFutureExceptionHandler
@EnableDiscoveryClient
public class ApplicationConsumer {
    public static void main(String[] args) {
        SpringApplication.run(ApplicationConsumer.class, args);
    }
}
