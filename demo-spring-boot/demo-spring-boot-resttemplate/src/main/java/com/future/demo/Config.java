package com.future.demo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.*;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
@Configuration
public class Config {
    /**
     *
     * @return
     */
    @Bean
    RestTemplate restTemplate() {
        ClientHttpRequestFactory factory =
                new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory());
        RestTemplate restTemplate = new RestTemplate(factory);
        List<ClientHttpRequestInterceptor> interceptors = restTemplate.getInterceptors();
        if (CollectionUtils.isEmpty(interceptors)) {
            interceptors = new ArrayList<>();
        }
        interceptors.add(new LoggingInterceptor());
        restTemplate.setInterceptors(interceptors);
        return restTemplate;
    }
}
