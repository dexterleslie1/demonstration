package com.future.demo.gateway.config;

import com.future.demo.common.feign.UserFeignIntranet;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(
        clients = {
                UserFeignIntranet.class
        }
)
public class ConfigFeign {
}
