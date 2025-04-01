package com.future.demo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class MyBean6Config {
    // 多个MyBean6存在，在使用@Autowired根据类型注入MyBean6时会报告错误，使用@Primary指定默认的bean解决问题
    @Primary
    @Bean
    public MyBean6 myBean61() {
        return new MyBean6();
    }

    @Bean
    public MyBean6 myBean62() {
        return new MyBean6();
    }
}
