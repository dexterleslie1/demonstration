# `spring-boot`自定义属性

> 例子详细请参考 [链接](https://github.com/dexterleslie1/demonstration/tree/master/demo-spring-boot/demo-spring-boot-configurationproperties)

借助`@ConfigurationProperties`定义`java`对应的自定义属性`bean`，其中通过`@Validated + @NotEmpty + @NotNull`启用属性值验证特性

```java
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

```

```java
@Data
public class ClientProperty {
    private String id;
    private String name;
}
```

`application.properties`配置自定义属性

```properties
# 自定义属性
spring.future.common.p1=v1
spring.future.common.p2[0]=v21
spring.future.common.p2[1]=v22

spring.future.common.clients[0].id=id1
spring.future.common.clients[0].name=name1
spring.future.common.clients[1].id=id2
spring.future.common.clients[1].name=name2
```

借助`@EnableConfigurationProperties`启用`MyProperties`加载`application.properties`中的自定义属性

```java
@Configuration
// 启用MyProperties加载application.properties中的自定义属性值并注入MyProperties实例到spring容器中
@EnableConfigurationProperties(MyProperties.class)
public class MyConfig {
}
```

使用`application.properties`中配置的属性值

```java
@RestController
@RequestMapping("/api/v1")
public class ApiController {

    // 注入并使用自定义属性
    @Resource
    MyProperties myProperties;

    @GetMapping("test1")
    ObjectResponse<String> test1() {
        return ResponseUtils.successObject("p1=" + this.myProperties.getP1() + ",p2=" + this.myProperties.getP2());
    }
}
```

