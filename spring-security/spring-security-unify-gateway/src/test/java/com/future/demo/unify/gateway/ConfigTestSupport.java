package com.future.demo.unify.gateway;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class ConfigTestSupport {
    @Bean
    public RestTemplate restTemplate() {
//        ClientHttpRequestFactory factory =
//                new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory());
//        RestTemplate restTemplate = new com.yyd.common.web.client.RestTemplate(factory);
//        List<ClientHttpRequestInterceptor> interceptors = restTemplate.getInterceptors();
//        if (CollectionUtils.isEmpty(interceptors)) {
//            interceptors = new ArrayList<>();
//        }
//        interceptors.add(new LoggingInterceptor());
//        restTemplate.setInterceptors(interceptors);
//        RestTemplate restTemplate = new com.yyd.common.web.client.RestTemplate(factory);
//        List<ClientHttpRequestInterceptor> interceptors = restTemplate.getInterceptors();
//        if (CollectionUtils.isEmpty(interceptors)) {
//            interceptors = new ArrayList<>();
//        }
//        interceptors.add(new LoggingInterceptor());
//        restTemplate.setInterceptors(interceptors);
//        return restTemplate;
        return new com.yyd.common.web.client.RestTemplate();
    }
}
