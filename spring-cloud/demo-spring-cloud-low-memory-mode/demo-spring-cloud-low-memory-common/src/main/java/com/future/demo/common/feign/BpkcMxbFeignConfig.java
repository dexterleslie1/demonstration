package com.future.demo.common.feign;

import feign.Logger;
import feign.Retryer;
import org.springframework.context.annotation.Bean;

public class BpkcMxbFeignConfig {
    @Bean
    Logger.Level feignLogLevel() {
        return Logger.Level.NONE;
    }

    @Bean
    Retryer retryer() {
        return Retryer.NEVER_RETRY;
    }
}
