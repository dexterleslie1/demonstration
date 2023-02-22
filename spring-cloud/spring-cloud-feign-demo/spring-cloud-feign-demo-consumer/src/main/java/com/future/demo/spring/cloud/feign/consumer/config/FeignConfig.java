package com.future.demo.spring.cloud.feign.consumer.config;

import feign.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {
    @Bean
    Logger.Level feignLogLevel() {
        return Logger.Level.FULL;
    }
}
