package com.future.demo.gateway;

import com.future.demo.architecture.common.feign.HelloworldClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(clients = {HelloworldClient.class})
public class ApplicationGateway {
    public static void main(String[] args) {
        SpringApplication.run(ApplicationGateway.class, args);
    }
}