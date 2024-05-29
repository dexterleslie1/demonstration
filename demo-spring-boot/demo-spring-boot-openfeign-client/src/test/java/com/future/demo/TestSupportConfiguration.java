package com.future.demo;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(
        clients = {
                TestSupportApiFeign.class
        }
)
public class TestSupportConfiguration {
}
