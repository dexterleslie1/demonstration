package com.future.demo.pkg1;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * 此配置统一导入TestService1Configuration和TestService2Configuration
 * 展示一个配置文件可以统一导入组件内部多个XXXConfiguration
 */
@Import({TestService1Configuration.class, TestService2Configuration.class})
public class TestServiceAllConfiguration {
}
