# `spring-boot`模拟引用第三方库中包含`RestController`

> 例子详细请参考 [链接](https://github.com/dexterleslie1/demonstration/tree/master/demo-spring-boot/demo-spring-boot-sim-external-restcontroller)
>
> 例子中包括`demo-third-party-lib`和`demo-tester`两个子项目，其中`demo-third-party-lib`用于模拟第三方依赖库，`demo-tester`用于测试引入`demo-third-party-lib`第三方依赖库。`demo-third-party-lib`中的`MyXxxController`定义了`/api/v1/test1`接口并通过`EnableXxxThirdParty`注解暴露给调用者应用。
>
> 运行例子的步骤：
>
> 1. 编译并安装`demo-third-party-lib`到本地`maven`
>
>    ```bash
>    mvn package install
>    ```
>
> 2. 使用`IntelliJ IDEA`运行`demo-tester`的测试用例

创建`MyXxxController`并定义`/api/v1/test1`接口

```java
// 此Controller用于当作第三方库被动态引用测试
@RestController
@RequestMapping("/api/v1")
public class MyXxxController {
    @GetMapping("test1")
    ObjectResponse<String> test1() {
        return ResponseUtils.successObject("Hello world!");
    }
}
```

`MyThirdPartyConfiguration`手动创建`MyXxxController`实例以注册`/api/v1/test1`接口到`spring`容器中

```java
public class MyThirdPartyConfiguration {
    // 手动创建Controller并注入到spring容器中，否则在引用这个库的应用中不能自动扫描并实例化相关Controller
    @Bean
    MyXxxController myXxxController() {
        return new MyXxxController();
    }
}
```

外部应用通过注解`@EnableXxxThirdParty`注册`/api/v1/test1`接口到`spring`容器中

```java
// 调用这个注解启用此库
@Retention(value = java.lang.annotation.RetentionPolicy.RUNTIME)
@Target(value = {java.lang.annotation.ElementType.TYPE})
@Documented
@Import({MyThirdPartyConfiguration.class})
public @interface EnableXxxThirdParty {

}
```

`demo-tester`中引用`demo-third-party-lib`依赖

```xml
<dependency>
    <groupId>com.future.demo</groupId>
    <artifactId>demo-third-party-lib</artifactId>
    <version>1.0.0</version>
</dependency>
```

`demo-tester`中启用`demo-third-party-lib`注册`/api/v1/test1`接口

```java
@SpringBootApplication
// 启用第三方库（实例化相关controller并注入到spring容器中）
@EnableXxxThirdParty
public class Application {
    public static void main(String []args){
        SpringApplication.run(Application.class, args);
    }
}
```

