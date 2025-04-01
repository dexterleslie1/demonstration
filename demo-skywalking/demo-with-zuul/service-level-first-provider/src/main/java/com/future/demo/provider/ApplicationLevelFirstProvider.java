package com.future.demo.provider;

import com.future.demo.gateway.common.feign.ServiceLevelSecondProviderClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableEurekaClient
@SpringBootApplication
@EnableFeignClients(
        clients = {
                ServiceLevelSecondProviderClient.class
        }
)
public class ApplicationLevelFirstProvider {
    public static void main(String[] args) {
        SpringApplication.run(ApplicationLevelFirstProvider.class, args);
    }
}