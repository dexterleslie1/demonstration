package com.future.demo.zuul;

import com.future.demo.feign.HelloworldClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableZuulProxy
@EnableFeignClients(clients = {HelloworldClient.class})
@EnableDiscoveryClient
public class ApplicationZuul {
    public static void main(String[] args) {
        SpringApplication.run(ApplicationZuul.class, args);
    }
}