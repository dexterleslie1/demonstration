package com.future.demo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MyBeanLifeCycleConfig {

    @Bean(initMethod = "initMethod", destroyMethod = "destroyMethod")
    public MyBeanLifeCycle myBean() {
        return new MyBeanLifeCycle();
    }
}
