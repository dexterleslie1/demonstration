package com.future.demo;

import com.future.common.exception.EnableFutureExceptionHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableFutureExceptionHandler
public class Application {
    public static void main(String []args){
        SpringApplication.run(Application.class, args);
    }
}
