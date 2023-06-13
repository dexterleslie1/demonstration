package com.future.demo.spring.cloud.feign.consumer.config;

import com.yyd.common.feign.CustomizeErrorDecoder;
import feign.Logger;
import feign.RequestInterceptor;
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

    @Bean
    RequestInterceptor requestInterceptor() {
        return new MyRequestInterceptor();
    }
}
