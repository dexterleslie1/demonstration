package com.future.demo.java;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {
    @Bean
    @ConditionalOnProperty(prefix = "spring.boot.test.demo", value="testing", havingValue = "false", matchIfMissing = true)
    TestService testService() {
        TestService service = new TestService();
        return service;
    }

    @Bean
    MyService1 myService1() {
        return new MyService1();
    }
}
