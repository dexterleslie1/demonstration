package com.future.demo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MyBean10Config {
    @Bean
    public MyBean10 myBean10() {
        return new MyBean10();
    }
}
