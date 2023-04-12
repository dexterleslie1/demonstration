package com.future.demo.spring.boot.test;

import org.springframework.context.annotation.Bean;

public class AutoConfigTestService {
    @Bean
    TestService testService() {
        return new TestService();
    }
}
