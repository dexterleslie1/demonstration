package com.future.demo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class MyDatasourceConfig {
    @Profile({"dev", "default"})
    @Bean
    public MyDatasource myDevDatasource() {
        return new MyDatasource("dev-ds");
    }

    @Profile("test")
    @Bean
    public MyDatasource myTestDatasource() {
        return new MyDatasource("test-ds");
    }

    @Profile("prod")
    @Bean
    public MyDatasource myProdDatasource() {
        return new MyDatasource("prod-ds");
    }
}
