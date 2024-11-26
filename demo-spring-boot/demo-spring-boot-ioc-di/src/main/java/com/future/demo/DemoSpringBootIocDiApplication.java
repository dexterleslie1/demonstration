package com.future.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

// 指定扫描com.future.demo和com.future.demo1包下的所有注解，否则会报告myBean3没有定义
@ComponentScan(basePackages = {"com.future.demo", "com.future.demo1"})
@SpringBootApplication
public class DemoSpringBootIocDiApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoSpringBootIocDiApplication.class, args);
    }

    // 注册MyBean1到Spring容器中
    // 通过方法名注册，方法名即为Bean名称
    @Bean
    public MyBean1 myBean1() {
        return new MyBean1();
    }

    // 注册MyBean2到Spring容器中
    // 通过方法名注册，方法名即为Bean名称
    @Bean
    public MyBean1 myBean11() {
        return new MyBean1();
    }

    // 通过@Bean指定Bean名称
    @Bean("myBean12")
    public MyBean1 myBeanaa() {
        return new MyBean1();
    }
}
