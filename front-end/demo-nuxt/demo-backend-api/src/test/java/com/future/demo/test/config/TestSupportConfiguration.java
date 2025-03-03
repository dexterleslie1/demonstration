package com.future.demo.test.config;

import com.future.demo.test.feign.TestSupportDemoFeignClient;
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
