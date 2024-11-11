package com.future.demo.zuul;

import com.future.demo.gateway.common.feign.ServiceLevelSecondProviderClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableZuulProxy
@EnableFeignClients(clients = {ServiceLevelSecondProviderClient.class})
public class ApplicationZuul {
    public static void main(String[] args) {
        SpringApplication.run(ApplicationZuul.class, args);
    }
}