package com.future.demo;

import com.future.demo.feign.FeignClientProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(clients = {FeignClientProvider.class})
public class ApplicationConsumer {
    public static void main(String[] args) {
        SpringApplication.run(ApplicationConsumer.class, args);
    }
}
