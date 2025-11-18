package com.future.demo;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
// 指定properties文件的路径
@PropertySource("file:${user.home}/my.properties")
public class Config1 {
    @Getter
    @Value("${my.p1}")
    private String myP1;
}
