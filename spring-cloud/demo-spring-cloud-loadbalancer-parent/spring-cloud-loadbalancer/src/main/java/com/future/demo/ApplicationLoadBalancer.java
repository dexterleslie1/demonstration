package com.future.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class ApplicationLoadBalancer {
    public static void main(String[] args) {
        SpringApplication.run(ApplicationLoadBalancer.class, args);
    }
}