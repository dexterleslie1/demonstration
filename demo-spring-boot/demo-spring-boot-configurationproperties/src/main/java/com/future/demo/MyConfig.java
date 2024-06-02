package com.future.demo;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
// 启用MyProperties加载application.properties中的自定义属性值并注入MyProperties实例到spring容器中
@EnableConfigurationProperties(MyProperties.class)
public class MyConfig {
}
