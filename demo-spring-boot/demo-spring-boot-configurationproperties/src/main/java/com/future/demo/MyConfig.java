package com.future.demo;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
// 启用MyProperties加载自定义属性值
@EnableConfigurationProperties(MyProperties.class)
public class MyConfig {
}
