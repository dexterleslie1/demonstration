package com.future.demo.config;

import com.future.demo.properties.TestProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

// 当 TestProperties 类存在时，加载该配置类
@ConditionalOnClass(value = TestProperties.class)
// 启用自定义属性
@EnableConfigurationProperties(TestProperties.class)
public class DemoPluginAutoConfiguration {
}
