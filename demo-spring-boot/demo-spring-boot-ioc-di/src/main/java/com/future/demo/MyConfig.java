package com.future.demo;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.*;

@Configuration
public class MyConfig {
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    @Bean
    public MyBean1 myBean13() {
        return new MyBean1();
    }

    @Bean
    public MyBean4 myBean41() {
        return new MyBean4();
    }

    @Lazy
    @Bean
    public MyBean4 myBean42() {
        return new MyBean4();
    }

}
