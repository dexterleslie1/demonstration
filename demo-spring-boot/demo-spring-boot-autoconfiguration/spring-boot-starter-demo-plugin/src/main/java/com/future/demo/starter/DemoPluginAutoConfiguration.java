package com.future.demo.starter;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

// 当 TestPropertiesPrinter 类存在时，加载该配置类
@ConditionalOnClass(value = TestPropertiesPrinter.class)
// 启用自定义属性
@EnableConfigurationProperties(value = TestPropertiesPrinter.TestProperties.class)
public class DemoPluginAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean
    TestPropertiesPrinter testPropertiesPrinter() {
        return new TestPropertiesPrinter();
    }
}
