package com.future.demo.mybatis.plus;

import com.future.common.exception.EnableFutureExceptionHandler;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.future.demo.mybatis.plus.mapper")
@EnableFutureExceptionHandler
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
