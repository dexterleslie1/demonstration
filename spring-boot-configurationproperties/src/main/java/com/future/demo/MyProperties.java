package com.future.demo;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

// 表示此自定义属性启用验证
// https://reflectoring.io/validate-spring-boot-configuration-parameters-at-startup/
@Validated
// 默认读取application.properties文件中自定义属性
// 自定义属性以spring.future.common开头
@ConfigurationProperties(prefix = "spring.future.common")
@Data
public class MyProperties {
    // 表示application.properties中要配置有值
    @NotEmpty
    // 表示application.properties中需要存在此配置，但可以为空字符串
    // 如果不存在应用不能启动
    @NotNull
    private String p1;

    @NotNull
    private List<String> p2;

    private List<ClientProperty> clients;
}
