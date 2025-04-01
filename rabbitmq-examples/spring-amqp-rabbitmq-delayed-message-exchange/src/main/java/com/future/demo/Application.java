package com.future.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 *
 */
@SpringBootApplication
public class Application {
    /**
     *
     * @param args
     */
    public static void main(String []args) {
        SpringApplication.run(Application.class, args);
    }
}
