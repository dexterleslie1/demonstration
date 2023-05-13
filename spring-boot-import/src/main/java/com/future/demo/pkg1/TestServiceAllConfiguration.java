package com.future.demo.pkg1;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * 此配置统一导入TestService1Configuration和TestService2Configuration
 * 模拟一个配置文件可以统一组件内部很多配置导入
 */
@Import({TestService1Configuration.class, TestService2Configuration.class})
@Configuration
public class TestServiceAllConfiguration {
}
