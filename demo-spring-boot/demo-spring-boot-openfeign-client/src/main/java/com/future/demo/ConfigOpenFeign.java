package com.future.demo;

import com.future.common.feign.CustomizeErrorDecoder;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConfigOpenFeign {
    /**
     * openfeign支持自动检查并抛出业务异常不需要编写代码判断errorCode是否不等于0
     *
     * @return
     */
    @Bean
    ErrorDecoder errorDecoder() {
        return new CustomizeErrorDecoder();
    }

}