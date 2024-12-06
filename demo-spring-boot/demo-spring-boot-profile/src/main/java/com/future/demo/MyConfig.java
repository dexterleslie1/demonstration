package com.future.demo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MyConfig {
    @Value("${my.p1}")
    String p1;

    @Value("${my.p2}")
    String p2;
}
