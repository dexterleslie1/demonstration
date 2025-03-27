# `SpringBoot`



## 打包和部署



### 非 Docker

详细用法请参考示例 `https://gitee.com/dexterleslie/demonstration/tree/master/demo-spring-boot/demo-spring-boot-mvc`

打包

```bash
./mvnw package -Ddockerfile.skip=false
```

启动应用

```bash
java -jar target/demo-spring-boot-mvc-0.0.1-SNAPSHOT.jar
```

访问`http://localhost:8080/hello`返回 Hello! 表示应用运行正常



### 基于 Docker

详细用法请参考示例 `https://gitee.com/dexterleslie/demonstration/tree/master/demo-spring-boot/demo-spring-boot-mvc`

Dockerfile 内容如下：

```dockerfile
#FROM openjdk:8-jdk-slim
FROM openjdk:17-jdk-slim

ADD target/demo.jar /usr/share/demo.jar

ENV JAVA_OPTS=""

ENTRYPOINT java ${JAVA_OPTS} -jar /usr/share/demo.jar --spring.profiles.active=prod

```

docker-compose.yaml 内容如下：

```yaml
version: "3.0"

services:
  demo:
    build:
      context: .
      dockerfile: Dockerfile
    environment:
      - JAVA_OPTS= -Xmx512m
      - TZ=Asia/Shanghai
    image: registry.cn-hangzhou.aliyuncs.com/future-public/demo-spring-boot-mvc
    ports:
      - "8080:8080"
```

build.sh 内容如下：

```bash
#!/bin/bash

set -e

./mvnw package -Dmaven.test.skip=true

docker compose build
```

push.sh 内容如下：

```bash
#!/bin/bash

set -e

docker compose push
```



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

Spring Boot自动配置能够根据应用程序所使用的框架、库或环境条件，自动配置相应的功能和组件。例如，当应用程序添加了数据库相关的依赖时，Spring Boot可以自动配置数据库连接池、事务管理等组件，无需手动配置。



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



### Profile 环境切换

详细用法请参考示例`https://gitee.com/dexterleslie/demonstration/tree/master/demo-spring-boot/demo-spring-boot-profile`

#### 使用 @Profile 注解指定特定的 Profile 环境才创建 bean

```java
package com.future.demo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class MyDatasourceConfig {
    // active profile为dev或default时，创建该bean
    @Profile({"dev", "default"})
    @Bean
    public MyDatasource myDevDatasource() {
        return new MyDatasource("dev-ds");
    }

    @Profile("test")
    @Bean
    public MyDatasource myTestDatasource() {
        return new MyDatasource("test-ds");
    }

    @Profile("prod")
    @Bean
    public MyDatasource myProdDatasource() {
        return new MyDatasource("prod-ds");
    }
}

```



#### properties 文件配置各个 Profile 配置

application-dev.properties 内容如下：

```properties
my.p2=dev2
```

application-test.properties 内容如下：

```properties
my.p2=test2
```

application-pod.properties 内容如下：

```properties
my.p2=prod2
```



#### yaml 文件配置各个 Profile 配置

application.yaml 内容如下：

```yaml
---
# 开发环境profile
my:
  p1: dev1
spring:
  profiles: dev

---
# 生产环境profile
my:
  p1: prod1
spring:
  profiles: prod

---
# 测试环境profile
my:
  p1: test1
spring:
  profiles: test
```



#### 切换 Profile 方法

方法1：在单元测试时使用 @ActiveProfiles("test") 切换 Profile

```java
@SpringBootTest(classes = Application.class)
@ActiveProfiles("test")
public class ApplicationTests {
```

方法2：在 application.properties 配置中使用 spring.profiles.active=dev 切换 Profile

```properties
spring.profiles.active=dev
```

方法3：在 java 命令行中切换 Profile

```bash
java -jar myapp.jar --spring.profiles.active=dev
```

方法4：编程式切换 Profile

>`https://stackoverflow.com/questions/31267274/spring-boot-programmatically-setting-profiles`

```java
// https://stackoverflow.com/questions/31267274/spring-boot-programmatically-setting-profiles
SpringApplication application = new SpringApplication(Application.class);
application.setAdditionalProfiles(springProfile);
context = application.run();
```



### 自定义 Starter

详细用法请参考`https://gitee.com/dexterleslie/demonstration/tree/master/demo-spring-boot/demo-spring-boot-starter`



#### 自定义 Starter 插件

创建普通的 SpringBoot 项目

通过 @ConfigurationProperties(prefix = "com.future.demo.test") 自定义插件支持的配置属性

```java
package com.future.demo.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
import java.util.Map;

@Data
@ConfigurationProperties(prefix = "com.future.demo.test")
public class TestProperties {
    private String prop1;
    private int prop2;
    private List<String> prop3;
    private Map<String, String> prop4;
    private NestedTestProperties nested;

    @Data
    public static class NestedTestProperties {
        private String prop1;
        private int prop2;
    }
}

```

定义 service、controller 等组件

```java
package com.future.demo.service;

import com.future.demo.properties.TestProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TestService {
    @Autowired
    TestProperties testProperties;

    public TestProperties getTestProperties() {
        return testProperties;
    }
}

package com.future.demo.controller;

import com.future.common.http.ObjectResponse;
import com.future.demo.properties.TestProperties;
import com.future.demo.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    @Autowired
    private TestService testService;

    @GetMapping("/")
    public ObjectResponse<TestProperties> index() {
        ObjectResponse<TestProperties> response = new ObjectResponse<>();
        response.setData(testService.getTestProperties());
        return response;
    }
}

```

插件自动配置类 DemoPluginAutoConfiguration

```java
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

```

配置`spring`自动扫描入口并自动配置插件的`/src/main/resources/META-INF/spring.factories`

```properties
# 引入依赖后spring会自动扫描这个入口自动配置插件
org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
  com.future.demo.config.DemoPluginAutoConfiguration
```

编译并安装插件到本地 maven 中

```bash
./mvnw install
```



#### 引用自定义 Starter 插件

maven 依赖中引用插件

```xml
<!-- 引用自定义插件 -->
<dependency>
    <groupId>com.future.demo</groupId>
    <artifactId>demo-plugin-spring-boot-starter</artifactId>
    <version>1.0.0</version>
</dependency>
```

测试引用的插件

```java
package com.future.demo;

import com.future.demo.service.TestService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class})
@AutoConfigureMockMvc
public class ApplicationTests {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    TestService testService;

    @Test
    public void contextLoads() throws Exception {
        this.mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().string("{\"errorCode\":0,\"errorMessage\":null,\"data\":{\"prop1\":\"astring1\",\"prop2\":18080,\"prop3\":[\"av1\",\"av2\"],\"prop4\":{\"k1\":\"avv1\",\"k2\":\"avv2\"},\"nested\":{\"prop1\":\"anv1x\",\"prop2\":18090}}}"));

        Assertions.assertEquals("astring1", this.testService.getTestProperties().getProp1());
        Assertions.assertEquals(2, this.testService.getTestProperties().getProp3().size());
        Assertions.assertEquals("avv1", this.testService.getTestProperties().getProp4().get("k1"));
        Assertions.assertEquals("anv1x", this.testService.getTestProperties().getNested().getProp1());
        Assertions.assertEquals(18090, this.testService.getTestProperties().getNested().getProp2());
    }
}

```



## 生命周期

>示例详细用法请参考`https://gitee.com/dexterleslie/demonstration/tree/master/demo-spring-boot/demo-spring-boot-lifecycle`



### SpringApplicationRunListener（全阶段）

SpringApplicationRunListener是Spring Boot框架中的一个重要接口，它主要用于监听Spring Boot应用启动过程中的不同阶段。通过实现这个接口，开发者可以在应用启动的过程中插入自定义的逻辑，如日志记录、性能监控或额外的资源加载等。以下是对SpringApplicationRunListener的详细解析：

**一、接口定义与功能**

SpringApplicationRunListener接口定义了一系列回调方法，这些方法在应用启动的不同阶段被调用。这些阶段包括：

1. **准备环境（EnvironmentPrepared）**：在读取应用程序配置、解析命令行参数后，准备运行环境。此时，可以访问和修改应用程序的环境属性。
2. **准备上下文（ContextPrepared）**：在应用上下文被创建并准备好但尚未刷新时。此时，可以访问和修改应用程序上下文的配置。
3. **上下文加载完成（ContextLoaded）**：在应用上下文加载完成但还未启动时。此时，可以执行一些在上下文刷新之前的自定义逻辑。
4. **上下文启动（Started）**：应用上下文刷新并启动。此时，可以执行一些在所有Bean初始化之后的操作。
5. **运行完成（Running/Ready）**：整个应用完全启动并准备处理请求。此时，可以执行一些应用程序启动后的最终检查或操作。注意，在Spring Boot 2.6及以上版本中，`running`方法被`ready`方法替换，以更准确地表示应用已经完全准备好处理外部请求的状态。
6. **启动失败（Failed）**：应用在启动过程中发生错误或异常。此时，可以处理启动失败的情况，如记录错误日志、发送通知等。

**二、实现与注册**

要实现SpringApplicationRunListener接口，开发者需要完成以下步骤：

1. **创建实现类**：创建一个类并实现SpringApplicationRunListener接口，同时重写其定义的回调方法。在方法中插入自定义的逻辑。
2. **注册监听器**：要使Spring Boot能够发现并使用自定义的监听器，需要在`META-INF/spring.factories`文件中进行注册。通常，Spring Boot会自动加载该文件中的配置，并将注册的监听器添加到应用启动流程中。

**三、使用示例**

以下是一个简单的SpringApplicationRunListener实现示例：

```java
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringApplicationRunListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import java.time.Duration;
 
public class CustomSpringApplicationRunListener implements SpringApplicationRunListener {
 
    public CustomSpringApplicationRunListener(SpringApplication application, String[] args) {
        // 构造函数中可以访问SpringApplication实例和启动参数
    }
 
    @Override
    public void starting() {
        System.out.println("应用正在启动...");
    }
 
    @Override
    public void environmentPrepared(ConfigurableEnvironment environment) {
        System.out.println("环境已经准备好...");
    }
 
    @Override
    public void contextPrepared(ConfigurableApplicationContext context) {
        System.out.println("上下文已准备...");
    }
 
    @Override
    public void contextLoaded(ConfigurableApplicationContext context) {
        System.out.println("上下文已加载...");
    }
 
    @Override
    public void started(ConfigurableApplicationContext context, Duration timeTaken) {
        System.out.println("应用已启动! 启动耗时: " + timeTaken.toMillis() + " 毫秒");
    }
 
    @Override
    public void ready(ConfigurableApplicationContext context, Duration timeTaken) {
        System.out.println("应用已准备就绪! 启动耗时: " + timeTaken.toMillis() + " 毫秒");
    }
 
    @Override
    public void failed(ConfigurableApplicationContext context, Throwable exception) {
        System.out.println("应用启动失败: " + exception.getMessage());
    }
}
```

然后，在`META-INF/spring.factories`文件中注册该监听器：

```
org.springframework.boot.SpringApplicationRunListener=\
com.example.CustomSpringApplicationRunListener
```

**四、注意事项**

1. **版本兼容性**：不同版本的Spring Boot可能对SpringApplicationRunListener的回调方法有所调整，因此在使用时需要确保与所使用Spring Boot版本的兼容性。
2. **性能影响**：在应用启动过程中插入自定义逻辑可能会对启动性能产生影响，因此需要根据实际需求谨慎使用。
3. **错误处理**：在实现自定义逻辑时，需要注意异常处理，以避免因异常导致应用启动失败。

综上所述，SpringApplicationRunListener为开发者提供了在应用启动过程中插入自定义逻辑的灵活机制，有助于实现更丰富的应用启动行为和监控需求。



### ApplicationListener（全阶段）

ApplicationListener是Spring框架中的一个重要接口，它允许开发者监听并处理应用程序中的事件。以下是关于ApplicationListener的详细解析：

**一、接口定义与功能**

ApplicationListener接口定义了一个`onApplicationEvent`方法，该方法在监听到事件发布后被自动调用。事件发布者并不需要知道哪些监听器会监听该事件，也不需要关心监听器如何实现事件处理逻辑。这种机制实现了发布者与监听者之间的解耦，提高了系统的可扩展性和可维护性。

**二、事件机制的主要组成部分**

Spring框架中的事件机制主要包括以下几个组成部分：

1. **事件（Event）**：事件是应用程序中可能发生的事情，它通常是一个继承自`ApplicationEvent`的类。开发者可以定义自己的事件类，以便在应用程序中传递关键信息。
2. **事件发布者（ApplicationEventPublisher）**：事件发布者负责发布事件。在Spring中，`ApplicationEventPublisher`接口是事件发布者的标准接口，它定义了发布事件的方法`publishEvent(Event event)`。Spring中的容器，如`ApplicationContext`，实现了这个接口，允许在应用程序中发布事件。
3. **事件监听器（ApplicationListener）**：事件监听器负责监听并处理特定类型的事件。`ApplicationListener`接口是事件监听器的标准接口，它定义了用于处理事件的方法`onApplicationEvent(Event event)`。开发者可以实现这个接口，以便在事件发生时执行自定义的逻辑。
4. **事件广播器（ApplicationEventMulticaster）**：事件广播器是框架内部的组件，负责将事件分发给所有注册的监听器。Spring提供了多个事件广播器的实现，其中`SimpleApplicationEventMulticaster`是一个简单的单线程实现。

**三、使用方法**

要使用ApplicationListener，开发者需要实现该接口，并将实现类注册到Spring容器中。当容器中有相应的事件触发时，Spring会自动调用监听器的`onApplicationEvent`方法。

**四、自定义事件与监听器**

开发者可以自定义事件和监听器，以满足特定的业务需求。自定义事件需要继承`ApplicationEvent`类，并在构造函数中传递事件源。自定义监听器需要实现`ApplicationListener`接口，并在`onApplicationEvent`方法中编写事件处理逻辑。

**五、内置事件**

Spring框架提供了一些内置事件，这些事件在特定的时间点被自动发布。例如：

- **ContextRefreshedEvent**：当`ApplicationContext`被初始化或刷新时发布。
- **ContextClosedEvent**：当`ApplicationContext`被关闭时发布。
- **RequestHandledEvent**：这是一个web-specific事件，告诉所有bean HTTP请求已经被服务。

**六、应用场景**

ApplicationListener在Spring框架中有着广泛的应用场景，例如：

- **通知服务**：在完成某项操作后发布事件，由专门的服务监听并执行相应的动作，如发送邮件或短信通知。
- **日志记录**：发布日志相关的事件，可以被不同的监听器接收以进行日志处理。
- **异步处理**：在某个操作完成后发布事件，由另一个线程或服务来处理后续逻辑，如文件上传后的处理等。

**七、注意事项**

1. **避免循环依赖**：在事件处理过程中，避免创建与事件发布者或监听器本身存在循环依赖的Bean。
2. **性能影响**：由于事件监听器在事件发生时被自动调用，因此可能会对应用的性能产生影响。因此，在使用时需要谨慎考虑是否需要在事件发生时执行这些操作。
3. **异常处理**：在实现事件监听器时，需要注意异常处理，以避免因异常导致应用崩溃或不稳定。

综上所述，ApplicationListener是Spring框架中一个非常有用的接口，它允许开发者以解耦的方式监听并处理应用程序中的事件。通过合理使用ApplicationListener，可以提高系统的可扩展性和可维护性，并满足各种业务需求。



### BootstrapRegistryInitializer（引导初始化阶段）

BootstrapRegistryInitializer是Spring Boot框架中的一个重要组件，它主要用于在应用启动阶段初始化和注册一些在ApplicationContext准备好之前就需要被创建和共享的对象。以下是对BootstrapRegistryInitializer的详细解析：

**一、接口定义与功能**

BootstrapRegistryInitializer接口定义了一个`initialize`方法，该方法接收一个`BootstrapRegistry`类型的参数。`BootstrapRegistry`是一个简单的对象注册表，它在启动和环境后处理期间都可用，直到ApplicationContext准备好为止。这个注册表可以用于注册那些可能创建成本较高或在ApplicationContext可用之前就需要被共享的实例。

**二、实现与加载**

1. **实现接口**：

   BootstrapRegistryInitializer接口的实现类需要重写`initialize`方法，并在该方法中执行必要的初始化和注册操作。

2. **加载机制**：

   Spring Boot通过SpringFactoriesLoader机制来加载实现了BootstrapRegistryInitializer接口的类。这些类的全限定名会被配置在`META-INF/spring.factories`文件中，并且与`org.springframework.boot.BootstrapRegistryInitializer`这个key相关联。

   例如，在Spring Cloud Config中，就通过`BootstrapRegistryInitializer`将配置中心的相关信息注册到了Spring容器中。其对应的实现类是`ConfigClientRetryBootstrapper`，并且这个实现类会在`spring-cloud-config-client`这个jar包的`META-INF/spring.factories`文件中被配置。

**三、使用场景与示例**

BootstrapRegistryInitializer通常用于需要在ApplicationContext准备好之前就被创建和共享的场景。例如，在Spring Cloud Config中，客户端需要向配置中心（Config Server）发送请求来获取应用程序的配置信息。这些配置信息需要在ApplicationContext创建之前就被加载和解析，因此就可以通过实现BootstrapRegistryInitializer接口来完成这个任务。

**四、注意事项**

1. **版本兼容性**：

   不同版本的Spring Boot可能会对BootstrapRegistryInitializer的加载和初始化机制有所不同，因此在使用时需要确保与所使用Spring Boot版本的兼容性。

2. **性能影响**：

   由于BootstrapRegistryInitializer是在ApplicationContext准备好之前执行的，因此它可能会对应用的启动性能产生影响。因此，在使用时需要谨慎考虑是否需要在启动阶段执行这些操作。

3. **错误处理**：

   在实现BootstrapRegistryInitializer时，需要注意异常处理，以避免因异常导致应用启动失败。

**五、总结**

BootstrapRegistryInitializer是Spring Boot框架中的一个重要组件，它允许开发者在应用启动阶段初始化和注册一些需要在ApplicationContext准备好之前就被创建和共享的对象。通过实现这个接口，开发者可以在应用启动的过程中插入自定义的逻辑，以满足特定的需求。同时，也需要注意版本兼容性、性能影响和错误处理等方面的问题。



### ApplicationContextInitializer（IOC 容器初始化阶段）

ApplicationContextInitializer是Spring框架中的一个扩展接口，它在应用程序上下文（ApplicationContext）创建之前提供了自定义初始化的能力。通过实现该接口，开发者可以在应用程序上下文启动之前执行一些额外的配置或准备工作。以下是对ApplicationContextInitializer的详细解析：

**一、接口定义与功能**

ApplicationContextInitializer接口定义了一个`initialize`方法，该方法接收一个泛型参数`C extends ConfigurableApplicationContext`，表示正在创建的应用程序上下文。在该方法中，开发者可以对应用程序上下文进行各种自定义操作，例如添加属性源、注册Bean定义、设置环境变量等。

**二、应用场景**

ApplicationContextInitializer通常用于以下场景：

1. **动态加载配置**：在应用程序上下文创建之前加载一些动态的配置，例如从外部配置文件中读取配置信息并注入到Spring的环境中。
2. **执行额外的初始化逻辑**：如果有一些需要在应用程序上下文启动之前执行的初始化逻辑，例如初始化数据库连接池或启动一些后台任务，可以通过实现ApplicationContextInitializer来实现这些逻辑。

**三、实现与注册**

1. **实现接口**：

   创建一个类并实现ApplicationContextInitializer接口，同时重写`initialize`方法。在方法中执行所需的初始化逻辑。

2. **注册ApplicationContextInitializer**：

   要使Spring能够发现并使用自定义的ApplicationContextInitializer，可以通过以下三种方式进行注册：

   - 在spring.factories文件中配置：在 resources/META-INF/spring.factories 文件中添加配置，指定ApplicationContextInitializer的实现类。例如：

     ```
     org.springframework.context.ApplicationContextInitializer=com.example.demo.CustomApplicationContextInitializer
     ```

   - 代码注册：在创建SpringApplication实例后，通过调用 addInitializers 方法将自定义的ApplicationContextInitializer添加到SpringApplication中。例如：

     ```java
     SpringApplication springApplication = new SpringApplication(SpringbootApplication.class);
     springApplication.addInitializers(new CustomApplicationContextInitializer());
     springApplication.run();
     ```

   - 配置文件注册：在 application.properties 或 application.yml 配置文件中添加 context.initializer.classes 属性，指定ApplicationContextInitializer的实现类。例如：

     ```properties
     context.initializer.classes=com.example.demo.CustomApplicationContextInitializer
     ```

**四、执行顺序**

ApplicationContextInitializer的执行顺序可以通过实现`Ordered`接口或使用`@Order`注解来指定。如果没有指定顺序，则按照默认的顺序执行。

**五、注意事项**

1. **避免循环依赖**：在初始化过程中，避免创建与ApplicationContext本身存在循环依赖的Bean。
2. **性能影响**：由于ApplicationContextInitializer在ApplicationContext创建之前执行，因此可能会对应用的启动性能产生影响。因此，在使用时需要谨慎考虑是否需要在启动阶段执行这些操作。
3. **错误处理**：在实现ApplicationContextInitializer时，需要注意异常处理，以避免因异常导致应用启动失败。

**六、示例**

以下是一个简单的ApplicationContextInitializer实现示例：

```java
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
 
import java.util.HashMap;
import java.util.Map;
 
public class CustomApplicationContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
 
    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        // 自定义属性资源
        Map<String, Object> map = new HashMap<>();
        map.put("customProperty", "自定义属性");
 
        // 通过自动配置上下文获取环境对象
        ConfigurableEnvironment environment = applicationContext.getEnvironment();
 
        // 添加自定义属性源到环境对象中
        environment.getPropertySources().addLast(new MapPropertySource("mapPropertySource", map));
    }
}
```

通过上述方式，开发者可以在Spring应用程序启动之前执行自定义的初始化逻辑，以满足特定的需求。



### ApplicationRunner（Spring 应用就绪阶段）

ApplicationRunner是Spring Boot框架中的一个接口，它允许开发者在Spring Boot应用程序启动后执行特定的任务或逻辑。以下是关于ApplicationRunner的详细解析：

**一、接口定义与功能**

ApplicationRunner接口定义了一个`run`方法，该方法在Spring Boot应用程序启动完成后被调用。开发者可以在这个方法中编写希望在应用程序启动后执行的代码逻辑，如加载数据、调用外部服务或执行其他业务逻辑。

**二、使用方法**

要使用ApplicationRunner，开发者需要创建一个实现了ApplicationRunner接口的类，并实现其`run`方法。然后，将这个类注册为Spring的组件（通常是通过在类上添加`@Component`注解）。这样，当Spring Boot应用程序启动时，Spring会自动调用这个类的`run`方法。

**三、参数与访问启动参数**

`run`方法接受一个`ApplicationArguments`类型的参数，该参数提供了对启动参数的访问。通过`ApplicationArguments`，开发者可以获取命令行参数、选项和非选项参数等信息，从而更灵活地配置和初始化应用程序。

**四、应用场景**

ApplicationRunner在Spring Boot应用程序中有多种应用场景，包括但不限于：

1. **服务启动后数据初始化**：在应用启动时加载初始化数据，如将初始数据加载到数据库、从文件读取数据、缓存热点数据等。
2. **应用启动时加载配置信息**：在某些情况下，应用可能需要在启动时加载外部配置信息或数据库中的参数到内存中进行缓存。
3. **启动时验证环境配置**：在应用启动时验证环境配置的正确性，如验证许可证的有效性、检查系统环境变量等。
4. **启动定时任务**：在应用启动时重新激活或启动定时任务。

**五、执行顺序与多个ApplicationRunner**

如果应用程序中有多个实现了ApplicationRunner接口的类，并且这些类需要在特定的顺序下执行，开发者可以使用`@Order`注解来指定执行顺序。`@Order`注解的值越小，表示优先级越高，即越先执行。

**六、注意事项**

1. **异常处理**：在实现ApplicationRunner的`run`方法时，需要注意异常处理，以避免因异常导致应用崩溃或不稳定。
2. **性能影响**：由于ApplicationRunner在应用程序启动时执行，因此可能会对启动性能产生影响。开发者需要谨慎考虑在`run`方法中执行的操作是否必要，并尽量优化这些操作的性能。
3. **避免循环依赖**：在实现ApplicationRunner时，需要避免与Spring容器中的其他Bean产生循环依赖。

综上所述，ApplicationRunner是Spring Boot框架中一个非常有用的接口，它允许开发者在应用程序启动后执行特定的任务或逻辑。通过合理使用ApplicationRunner，开发者可以更灵活地配置和初始化应用程序，并满足各种业务需求。



### CommandLineRunner（Spring 应用就绪阶段）

CommandLineRunner是Spring Boot框架中的一个重要接口，它允许开发者在Spring Boot应用程序启动后执行特定的代码逻辑。以下是对CommandLineRunner的详细解释：

**一、接口定义与功能**

CommandLineRunner接口定义了一个`run`方法，该方法在Spring Boot应用程序启动完成后被自动调用。通过实现CommandLineRunner接口，开发者可以定义一些需要在应用程序启动后执行的初始化操作，例如加载初始化数据、启动后打印应用信息、启动异步任务等。

**二、使用方法**

要使用CommandLineRunner，开发者需要完成以下步骤：

1. 创建一个实现CommandLineRunner接口的类，并实现其`run`方法。
2. 在`run`方法中编写需要在应用程序启动后执行的代码逻辑。
3. 将这个类注册为Spring的组件，通常是通过在类上添加`@Component`注解。

这样，当Spring Boot应用程序启动时，Spring会自动调用这个类的`run`方法。

**三、参数与命令行参数**

`run`方法接受一个字符串数组参数，这个数组包含了应用程序启动时传递的命令行参数。这为开发者提供了一种在应用程序启动时动态配置或执行不同逻辑的机会。例如，可以根据命令行参数的不同来加载不同的配置文件或执行不同的初始化操作。

**四、应用场景**

CommandLineRunner在Spring Boot应用程序中有多种应用场景，包括但不限于：

1. **数据初始化**：在应用程序启动后加载初始化数据，如从数据库中加载配置信息或初始化一些全局变量。
2. **应用信息打印**：在应用程序启动后打印一些有用的信息，如应用程序的版本号、内存使用情况等，以帮助开发者了解应用程序的运行状态和性能。
3. **异步任务启动**：在应用程序启动后启动一些异步任务，如发送电子邮件或进行一些批处理操作。
4. **参数校验**：在实现CommandLineRunner接口时，可以进行一些参数校验，以确保应用程序在运行之前满足一些必要的条件或参数要求，从而提高应用程序的健壮性和安全性。

**五、与ApplicationRunner的区别**

CommandLineRunner与ApplicationRunner都是Spring Boot中用于在应用程序启动后执行特定逻辑的接口。它们的主要区别在于`run`方法的参数不同：

- CommandLineRunner的`run`方法接受一个字符串数组参数，该参数包含了应用程序启动时传递的命令行参数。
- ApplicationRunner的`run`方法则接受一个`ApplicationArguments`对象作为参数，该对象提供了对启动参数的更高级别的访问，包括选项和非选项参数等。

**六、注意事项**

1. **异常处理**：在实现CommandLineRunner的`run`方法时，需要注意异常处理，以避免因异常导致应用崩溃或不稳定。
2. **性能影响**：由于CommandLineRunner在应用程序启动时执行，因此可能会对启动性能产生影响。开发者需要谨慎考虑在`run`方法中执行的操作是否必要，并尽量优化这些操作的性能。
3. **执行顺序**：如果有多个实现了CommandLineRunner接口的类，并且这些类需要在特定的顺序下执行，开发者可以使用`@Order`注解来指定执行顺序。

综上所述，CommandLineRunner是Spring Boot框架中一个非常有用的接口，它允许开发者在应用程序启动后执行特定的代码逻辑。通过合理使用CommandLineRunner，开发者可以更灵活地配置和初始化应用程序，并满足各种业务需求。



## application.properties 中配置获取随机数

>`https://dev.to/mxglt/generate-random-values-in-your-properties-spring-boot-4ppf`

```properties
leaf.snowflake.port=${random.int%1000000000}
```

- leaf.snowflake.port 会随机生成一个整数并求余
- 此配置由 RandomValuePropertySource 解析



## spring-boot-devtools

### 介绍

spring-boot-devtools是Spring为开发者提供的一个热加载工具包，旨在提高开发人员的生产力，并加速Spring Boot应用程序的开发。以下是对spring-boot-devtools的详细介绍：

一、主要功能

1. **自动重新加载**：当应用程序中的代码或资源文件（如HTML、CSS、JavaScript等）发生变化时，spring-boot-devtools会自动重新加载应用程序，而无需手动重新启动服务器。这可以节省大量时间，尤其是在开发过程中进行代码调试和迭代时。
2. **实时监控**：提供了实时的应用程序监控功能，包括应用程序的运行状态、内存使用情况、线程状态等信息。这有助于开发人员快速了解应用程序的状态和性能，并及时发现和解决问题。
3. **自动配置**：可以根据开发环境自动配置应用程序，如启用H2数据库控制台、禁用安全等，以减少开发人员手动配置应用程序的需求。
4. **日志管理**：提供了一个集中的日志管理功能，可以显示应用程序的日志信息，并在出现问题时提供更详细的错误堆栈跟踪，有助于开发人员快速定位和解决问题。

二、配置方法

1. **引入依赖**：在Spring Boot项目的`pom.xml`文件中添加spring-boot-devtools的依赖项。需要设置`<scope>`为`runtime`，表示DevTools仅在运行时生效，不会打包到最终的可执行JAR文件中；同时设置`<optional>`为`true`，表示该依赖是可选的，不会传递到其他依赖项目中。

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-devtools</artifactId>
    <scope>runtime</scope>
    <optional>true</optional>
</dependency>
```

1. **配置文件**：在`application.yml`或`application.properties`文件中添加相关配置，以启用自动重启功能并指定监视的路径。例如：

```yaml
spring:
  devtools:
    restart:
      enabled: true # 启用自动重启功能
      additional-paths: src/main/java # 监视的路径
```

1. **设置自动重启更新**：确保在IDE中开启了自动编译功能，并配置快捷键以加快热部署的响应速度。例如，在IntelliJ IDEA中，可以使用快捷键Ctrl+Shift+A打开Registry，并搜索`compiler.automake.postpone.when.idle.less.than`，将其值设置为较小的数字（如500）。

三、注意事项

1. **性能影响**：spring-boot-devtools主要用于开发环境，因为它可能会影响应用程序的性能。因此，在生产环境中不应部署DevTools。
2. **安全性**：确保不要在生产环境中部署DevTools，因为它可能打开一些不安全的端点。
3. **缓存管理**：spring-boot-devtools默认禁用缓存选项，以避免在开发过程中因缓存而导致无法看到刚刚做出的更改。如果不希望应用属性默认值，可以在`application.properties`中将`spring.devtools.add-properties`设置为`false`。
4. **日志记录**：在开发Spring MVC和Spring WebFlux应用程序时，可能需要更多关于Web请求的信息。此时，可以将Web日志组启用为DEBUG日志记录，以获取有关传入请求、处理它的处理程序、响应结果和其他详细信息。

四、工作原理

spring-boot-devtools使用了两个ClassLoader类加载器来实现热部署。一个ClassLoader加载那些不会改变的类（如第三方Jar包），另一个ClassLoader（称为restart ClassLoader）加载会发生变化的类。当代码发生更改时，原来的restart ClassLoader被释放并重新创建一个新的restart ClassLoader来加载更改后的类。由于需要加载的类相对较少，因此可以实现较快的重启时间（通常在5秒以内）。

综上所述，spring-boot-devtools是一个功能强大的工具，可以显著提高Spring Boot应用程序的开发效率。通过自动重新加载、实时监控、自动配置和日志管理等功能，开发人员可以更快地构建和调试应用程序。然而，在使用时需要注意其性能影响和安全性问题，并确保仅在开发环境中使用。



### 使用

>[参考链接](https://docs.spring.io/spring-boot/docs/current/reference/html/using-spring-boot.html#using-boot-devtools)
>
>[DevTools 无效的解决办法](https://blog.csdn.net/qq_34491508/article/details/83830075)

POM 添加如下配置：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-devtools</artifactId>
    <!-- 表示DevTools仅在运行时生效，不会打包到最终的可执行JAR文件中 -->
    <scope>runtime</scope>
    <optional>true</optional>
</dependency>
```

勾选 Settings > Build、Execution、Deployment > Compiler > Make project automatically

搜索并打开 Registry，在 Registry 中搜索 automake 并勾选 compiler.automake.allow.when.app.running（注意：在 IDEA 2021 之后，此设置已移至高级设置。文件 -> 设置... -> 高级设置，然后选中允许 Allow auto-make to start ...）

重启 IDEA

注意：编辑源代码后需要按 ctrl+s 才会触发 DevTools 热加载。

