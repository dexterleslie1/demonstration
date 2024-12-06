# `SpringBoot`



## 打包、部署、运维

使用 `https://gitee.com/dexterleslie/demonstration/tree/master/demo-spring-boot/demo-spring-boot-mvc` 测试 spring boot 应用打包、部署、运维

打包

```bash
./mvnw package -Ddockerfile.skip=false
```

启动应用

```bash
java -jar target/demo-spring-boot-mvc-0.0.1-SNAPSHOT.jar
```

访问`http://localhost:8080/hello`返回 Hello! 表示应用运行正常



## SpringBoot Starter

SpringBoot Starter是一组方便的依赖组合，用于简化Spring项目中的依赖管理。以下是对Spring Boot Starter的详细介绍：

**一、定义与功能**

1. **定义**：SpringBoot Starter是一组预先打包好的Maven依赖组合，提供了开发某一类功能所需要的所有依赖。这些Starter将常用的功能场景抽取出来，做成了一系列场景启动器，这些启动器帮助开发者导入了实现各个功能所需要依赖的全部组件。
2. **功能**：
   - 自动导入相关组件：开发者只需在项目中引入对应的Starter，Spring Boot就会自动导入实现该功能所需的所有依赖。
   - 自动配置：Starter还包含了自动配置的功能，开发者只需通过配置文件进行少量配置，即可使用相应的功能。

**二、命名规范与分类**

1. **命名规范**：
   - 官方Starter：通常命名为“spring-boot-starter-{name}”，例如spring-boot-starter-web。
   - 自定义Starter：遵循“{name}-spring-boot-starter”的格式，例如mybatis-spring-boot-starter。
2. **分类**：
   - 官方Starter：Spring Boot官方提供的，用于快速构建和配置特定类型应用程序的Starter。
   - 第三方Starter：由社区或第三方组织提供的，用于扩展Spring Boot功能的Starter。

**三、常用Starter示例**

1. **spring-boot-starter-web**：
   - 功能：用于构建Web应用程序，提供了Spring MVC和嵌入式Tomcat支持。
   - 引入方式：在pom.xml中添加对应的依赖。
   - 配置：通过application.properties或application.yml文件进行配置。
2. **spring-boot-starter-data-jpa**：
   - 功能：用于与数据库交互，提供了JPA支持，并集成了Hibernate。
   - 引入方式：同样在pom.xml中添加对应的依赖。
   - 配置：需要配置数据库连接信息、JPA方言等。
3. **spring-boot-starter-security**：
   - 功能：用于实现用户认证和授权功能，集成了Spring Security。
   - 引入与配置方式：与上述Starter类似。

**四、自定义Starter**

1. **定义**：自定义Starter是指开发者根据自己的需求，将常用的依赖和配置打包为一个Starter，以便在多个项目中进行复用和共享。
2. **创建流程**：
   - 创建一个新的Maven项目，并添加必要的依赖。
   - 编写自定义的业务类和自动配置类。
   - 在资源目录下创建spring.factories文件，并指定自动配置类的路径。
   - 将项目打包为jar文件，并发布到Maven仓库。
3. **使用方式**：在其他Spring Boot项目中，通过引入自定义Starter的依赖，并使用配置文件进行必要的配置，即可使用自定义Starter提供的功能。

**五、注意事项**

1. **选择合适的Starter**：根据项目需求选择合适的Starter，避免引入不必要的依赖和配置。
2. **了解默认配置**：在使用Starter时，要了解其默认包含的库和配置，以便根据需要进行调整和优化。
3. **自定义配置**：如果默认配置不满足需求，可以通过配置文件进行自定义配置。

综上所述，SpringBoot Starter是一种用于简化依赖管理和配置的方式，它通过将常用的功能场景抽取出来并打包为一系列场景启动器，帮助开发者快速构建和配置特定类型的应用程序。



## 依赖管理

在基于Spring Boot的Maven项目中，通常不需要指定starter的版本，原因在于Spring Boot的依赖管理机制。具体来说，这种机制的核心在于`spring-boot-starter-parent`和`spring-boot-dependencies`。

1. **spring-boot-starter-parent**：这是一个特殊的父POM（Project Object Model），它为Spring Boot项目提供了默认的依赖管理配置。当一个Spring Boot项目继承了`spring-boot-starter-parent`时，它会自动管理所有依赖的版本号，包括starter依赖。因此，在添加starter依赖时，可以省略版本号。
2. **spring-boot-dependencies**：这个模块在`spring-boot-starter-parent`中定义了一个`dependencyManagement`元素，用于集中管理所有需要的jar包及其版本号。`spring-boot-dependencies`包含了Spring Boot项目常用的依赖和它们的版本号。当在项目的`pom.xml`文件中添加一个starter依赖时，Maven会自动查找并使用在`spring-boot-dependencies`中定义的版本号。

这种机制的好处在于：

- 可以在一个地方统一管理所有的依赖版本号，避免了在多个地方重复指定版本号的繁琐操作。
- 降低了因版本号不一致而导致的问题。
- 减少了因版本不兼容导致的问题。

同时，虽然Spring Boot提供了默认的依赖管理配置，但开发者仍然可以通过在子POM中明确指定依赖的版本号来覆盖默认的版本号。这样可以确保项目的编译和运行时一致性，特别是当需要使用特定版本的依赖时。

综上所述，基于Spring Boot的Maven项目不需要指定starter的版本号，是因为Spring Boot通过`spring-boot-starter-parent`和`spring-boot-dependencies`提供了统一的依赖管理配置，简化了依赖管理过程。



### 修改依赖的默认版本

**通过依赖的`<version>`标签直接指定**

```xml
<!-- mariadb驱动依赖 -->
<dependency>
    <groupId>org.mariadb.jdbc</groupId>
    <artifactId>mariadb-java-client</artifactId>
    <version>3.4.0</version>
    <scope>runtime</scope>
</dependency>
```

**通过 properties 属性修改**

```xml
<properties>
    <mariadb.version>3.4.0</mariadb.version>
</properties>
```



## 自动配置



### 自定义属性

示例的详细用法请参考`https://gitee.com/dexterleslie/demonstration/tree/master/demo-spring-boot/demo-spring-boot-configurationproperties`

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



