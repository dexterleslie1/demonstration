package com.future.demo.config;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.config.MeterFilter;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MetricsFilterConfig {

    @Bean
    public MeterRegistryCustomizer<MeterRegistry> meterRegistryCustomizer() {
        return registry -> {
            // 添加全局过滤器
            registry.config()
                    // 过滤所有以 "kafka" 开头的指标
                    .meterFilter(MeterFilter.denyNameStartsWith("kafka"))
                    .meterFilter(MeterFilter.denyNameStartsWith("spring.kafka"))
                    .meterFilter(MeterFilter.denyNameStartsWith("lettuce.command"));
        };
    }
}