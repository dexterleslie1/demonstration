package com.future.demo.spring.boot.test;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:my-test.properties")
public class Config {
}
