package com.future.demo;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

// 需要使用@Configuration注解@Bean注解才能生效
@Configuration
public class MyBean2Config {
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    @Bean
    public MyBean2 myBean2() {
        return new MyBean2();
    }
}
