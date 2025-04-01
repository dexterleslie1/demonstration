package com.future.demo;

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
}
