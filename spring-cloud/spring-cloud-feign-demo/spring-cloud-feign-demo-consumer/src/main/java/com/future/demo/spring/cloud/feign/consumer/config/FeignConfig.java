package com.future.demo.spring.cloud.feign.consumer.config;

import com.future.common.feign.CustomizeErrorDecoder;
import feign.Logger;
import feign.RequestInterceptor;
import feign.Retryer;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {
    // 设置 OpenFeign 日志级别
    @Bean
    Logger.Level feignLogLevel() {
        return Logger.Level.FULL;
    }

    // 自定义 OpenFeign 错误解码器
    @Bean
    ErrorDecoder errorDecoder() {
        return new CustomizeErrorDecoder();
    }

    // 自定义 OpenFeign 请求拦截器
    @Bean
    RequestInterceptor requestInterceptor() {
        return new MyRequestInterceptor();
    }

    // 设置重试机制
    @Bean
    Retryer retryer() {
        // 不启用重试机制
        // return Retryer.NEVER_RETRY;
        // 每次重试的时间间隔为 1 秒，period 和 maxPeriod 设置为相等，最大重试次数为 3 次
        return new Retryer.Default(1000, 1000, 3);
    }
}
