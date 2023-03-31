package com.future.demo.spring.cloud.feign.consumer.config;

import feign.Logger;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {
    @Bean
    Logger.Level feignLogLevel() {
        return Logger.Level.FULL;
    }

    /**
     * openfeign自定义错误处理
     *
     * @return
     */
    @Bean
    ErrorDecoder errorDecoder() {
        return new CustomizeErrorDecoder();
    }
}
