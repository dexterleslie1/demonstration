package com.future.demo.pkg1;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TestService1Configuration {
    @Bean
    TestService1 testService1() {
        return new TestService1();
    }
}
