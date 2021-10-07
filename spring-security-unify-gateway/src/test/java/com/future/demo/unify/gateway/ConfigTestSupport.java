package com.future.demo.unify.gateway;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ConfigTestSupport {
    @Bean
    public RestTemplate restTemplate() {
        return new com.yyd.common.web.client.RestTemplate();
    }
}
