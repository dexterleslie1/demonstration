package com.future.demo;

import com.future.common.auth.EnableFutureAuthorization;
import com.future.common.exception.EnableFutureExceptionHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 应用的主函数启动入口
 */
@SpringBootApplication
@EnableFutureExceptionHandler
@EnableFutureAuthorization
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
