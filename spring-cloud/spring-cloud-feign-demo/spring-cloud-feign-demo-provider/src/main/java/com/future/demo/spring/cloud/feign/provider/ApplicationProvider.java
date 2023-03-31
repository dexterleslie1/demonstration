package com.future.demo.spring.cloud.feign.provider;

import com.yyd.common.exception.ExceptionController;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(value = {ExceptionController.class})
public class ApplicationProvider {
    public static void main(String[] args) {
        SpringApplication.run(ApplicationProvider.class, args);
    }
}
