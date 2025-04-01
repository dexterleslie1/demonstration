package com.future.demo.auth;

import com.yyd.common.exception.ExceptionController;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Import;

@EnableEurekaClient
@SpringBootApplication
@Import(value = {
        ExceptionController.class
})
public class ApplicationAuth {
    public static void main(String[] args) {
        SpringApplication.run(ApplicationAuth.class, args);
    }
}