package com.future.demo;

import com.yyd.common.config.EnableFutureExceptionHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author Dexterleslie.Chan
 */
@SpringBootApplication
@EnableFutureExceptionHandler
public class Application {
    /**
     *
     * @param args
     */
    public static void main(String []args){
        SpringApplication.run(Application.class, args);
    }
}
