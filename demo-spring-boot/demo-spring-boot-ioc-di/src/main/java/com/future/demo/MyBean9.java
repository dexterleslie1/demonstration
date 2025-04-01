package com.future.demo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

// @Value、@PropertySource用法
// 指定从my.properties文件中读取属性值，其中*表示从所有包路径下查找（包括第三方jar包）
@PropertySource("classpath*:my.properties")
@Component
public class MyBean9 {
    // 注入application.properties配置文件中的值
    @Value("${mybean9.name}")
    private String name;

    // SpEL表达式
    @Value("#{T(java.util.UUID).randomUUID().toString()}")
    private String id;

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }
}
