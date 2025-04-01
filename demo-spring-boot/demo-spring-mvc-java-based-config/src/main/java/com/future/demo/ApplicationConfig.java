package com.future.demo;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
// 等价于 xml 配置中的 <context:component-scan base-package="com.future.demo"/>
@ComponentScan({"com.future.demo"})
public class ApplicationConfig {
}
