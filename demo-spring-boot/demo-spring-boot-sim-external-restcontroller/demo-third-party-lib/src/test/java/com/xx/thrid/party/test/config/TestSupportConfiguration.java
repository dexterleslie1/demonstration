package com.xx.thrid.party.test.config;

import com.xx.thrid.party.test.feign.TestSupportDemoFeignClient;
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
