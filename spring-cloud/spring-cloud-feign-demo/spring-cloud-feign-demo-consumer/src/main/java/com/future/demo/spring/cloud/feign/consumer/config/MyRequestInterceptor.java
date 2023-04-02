package com.future.demo.spring.cloud.feign.consumer.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;

/**
 * 所有feign调用http头都注入my-header参数
 * https://developer.aliyun.com/article/1058305
 */
public class MyRequestInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate template) {
        template.header("my-header", "my-value");
    }
}
