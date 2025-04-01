package com.future.demo.http;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author Dexterleslie.Chan
 */
@SpringBootApplication
@ComponentScan(basePackages={"com.future.demo"})
public class Application {
    public static void main(String []args){
        SpringApplication.run(Application.class, args);
    }
}
