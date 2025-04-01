package com.future.demo.provider;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@EnableEurekaClient
@SpringBootApplication
public class ApplicationProvider {
    public static void main(String[] args) {
        SpringApplication.run(ApplicationProvider.class, args);
    }
}