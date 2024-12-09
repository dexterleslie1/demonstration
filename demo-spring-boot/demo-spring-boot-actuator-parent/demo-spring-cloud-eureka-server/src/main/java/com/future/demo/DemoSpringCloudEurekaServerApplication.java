package com.future.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class DemoSpringCloudEurekaServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoSpringCloudEurekaServerApplication.class, args);
    }

}
