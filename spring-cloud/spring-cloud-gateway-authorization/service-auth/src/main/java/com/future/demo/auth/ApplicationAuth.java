package com.future.demo.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@EnableEurekaClient
@SpringBootApplication
public class ApplicationAuth {
    public static void main(String[] args) {
        SpringApplication.run(ApplicationAuth.class, args);
    }
}