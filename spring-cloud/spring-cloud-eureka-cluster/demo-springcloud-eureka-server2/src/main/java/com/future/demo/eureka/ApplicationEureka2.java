package com.future.demo.eureka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@EnableEurekaServer
@SpringBootApplication
public class ApplicationEureka2 {
    public static void main(String[] args) {
        SpringApplication.run(ApplicationEureka2.class, args);
    }
}