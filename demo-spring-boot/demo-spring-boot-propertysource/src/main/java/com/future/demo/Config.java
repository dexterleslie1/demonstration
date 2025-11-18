package com.future.demo;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
// 加载自定义properties文件作为默认值
@PropertySource("classpath:my-test.properties")
public class Config {
}
