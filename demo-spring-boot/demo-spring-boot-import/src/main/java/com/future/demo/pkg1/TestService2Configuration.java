package com.future.demo.pkg1;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

public class TestService2Configuration {
    @Bean
    TestService2 testService2() {
        return new TestService2();
    }
}
