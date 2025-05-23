package com.future.demo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Dexterleslie.Chan
 */
@Configuration
public class Config {
    public static final String ExchangeName = "spring-amqp-fanout-exchange-demo";

    @Bean
    public AtomicInteger counter() {
        return new AtomicInteger();
    }
}
