package com.future.demo.config;

import com.future.demo.feign.TestSupportDemoFeignClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(
        clients = {
                TestSupportDemoFeignClient.class
        }
)
public class TestSupportConfiguration {
}
