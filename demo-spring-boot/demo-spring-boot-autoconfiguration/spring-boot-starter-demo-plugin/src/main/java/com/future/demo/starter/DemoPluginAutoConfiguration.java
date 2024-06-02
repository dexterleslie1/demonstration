package com.future.demo.starter;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(value = TestPropertiesPrinter.class)
@EnableConfigurationProperties(value = TestProperties.class)
public class DemoPluginAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean
    TestPropertiesPrinter testPropertiesPrinter() {
        return new TestPropertiesPrinter();
    }
}
