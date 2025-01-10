package com.future.demo;

import com.future.common.exception.EnableFutureExceptionHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableFutureExceptionHandler
public class ApplicationStorage {
    public static void main(String[] args) {
        SpringApplication.run(ApplicationStorage.class, args);
    }
}
