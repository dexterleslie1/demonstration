# `logback`配置和使用



## `slf4j mdc`

MDC（Mapped Diagnostic Context）是SLF4J（Simple Logging Facade for Java）等日志框架提供的一种轻量级日志跟踪工具。以下是关于SLF4J MDC的详细解释：

**一、MDC的定义与功能**

MDC是一个线程安全的存放诊断日志的容器，它允许开发者将一些特定的数据（如用户ID、请求ID、会话ID等）存储到当前线程的上下文中。这些数据可以在日志消息中使用，从而实现对日志的跟踪和区分。在高并发环境中，由于多个请求可能同时处理，日志消息可能会交错在一起。使用MDC，可以为每个请求分配一个唯一的标识，并将该标识添加到每条日志消息中，从而方便地区分和跟踪每个请求的日志。

**二、MDC的使用场景**

MDC在日志跟踪中非常有用，特别是在多线程或高并发应用中。以下是一些常见的使用场景：

1. **跟踪单个请求**：在接收到请求时生成一个唯一的请求ID（trace-id），并在整个执行链路中带上此唯一ID。通过MDC，可以将这个ID添加到每条日志消息中，从而方便地跟踪单个请求的日志。
2. **记录用户信息**：在日志中记录请求用户的IP地址、用户ID等信息，以便在出现问题时能够快速定位到具体的用户。
3. **日志过滤**：通过MDC中的唯一标识，可以方便地过滤出特定请求的日志，从而进行问题排查和分析。

**三、MDC的实现与配置**

MDC本身不提供传递trace-id的能力，真正提供能力的是MDCAdapter接口的实现。不同的日志框架（如Log4j、Logback等）都有自己对应的MDCAdapter实现。在日志配置中，可以使用特定的占位符（如`%X{trace-id}`）来获取并展示MDC中的数据。

以Logback为例，可以在logback.xml配置文件中使用`<pattern>`标签来定义日志的输出格式。在格式中，可以使用`%X{key}`来占位，其中`key`是MDC中存储数据的键名。当日志输出时，`%X{key}`会被替换为MDC中对应键名的值。

**四、MDC的注意事项**

1. **线程安全**：MDC是线程安全的，这意味着在多线程环境中，每个线程都可以有自己的MDC上下文，而不会相互干扰。
2. **数据清理**：在使用MDC时，需要注意在适当的时候清理数据。例如，在处理完一个请求后，应该从MDC中移除与该请求相关的数据，以避免数据泄露或占用不必要的内存。
3. **性能考虑**：虽然MDC提供了方便的日志跟踪功能，但在高并发环境中使用时，也需要注意性能问题。过多的日志输出和MDC操作可能会增加系统的开销，因此需要根据实际情况进行权衡和优化。

综上所述，MDC是SLF4J等日志框架提供的一种轻量级日志跟踪工具，它允许开发者将特定的数据存储到当前线程的上下文中，并在日志消息中使用这些数据。通过MDC，可以方便地跟踪和区分多线程或高并发应用中的单个请求日志。



### `mdc`使用

详细用法请参考`https://gitee.com/dexterleslie/demonstration/tree/master/demo-java/demo-library/demo-logback/demo-spring-boot-logback`

在`java`代码中设置`mdc`值

```java
MDC.put("my-mdc1", UUID.randomUUID().toString());
```

在`logback-spring.xml`配置文件中引用`mdc`

```xml
%X{my-mdc1}
```



## `logback`输出`JSON`格式日志

详细用法请参考`https://gitee.com/dexterleslie/demonstration/tree/master/demo-java/demo-library/demo-logback/demo-spring-boot-logback`

`maven`加入依赖用于输出`JSON`格式日志

```xml
<dependency>
    <groupId>net.logstash.logback</groupId>
    <artifactId>logstash-logback-encoder</artifactId>
    <version>6.6</version>
</dependency>
<dependency>
    <groupId>org.codehaus.janino</groupId>
    <artifactId>janino</artifactId>
</dependency>
```

`JSON appender`和`Logger`配置如下：

```xml
<!-- 测试json encoder -->
<appender name="jsonAppender" class="ch.qos.logback.core.ConsoleAppender">
    <encoder charset="UTF-8" class="net.logstash.logback.encoder.LogstashEncoder">
        <customFields>{"module": "my-module"}</customFields>
    </encoder>
</appender>

<!-- 测试json encoder -->
<logger name="jsonLogger" level="debug" additivity="false">
    <appender-ref ref="jsonAppender"/>
</logger>
```

`java`代码中使用`JSON logger`

```java
final static Logger jsonLogger = LoggerFactory.getLogger("jsonLogger");

// json encoder测试
jsonLogger.debug("测试json encoder");
```



## 普通`java maven`项目配置和使用`logback`

> `maven`配置`logback`参考`https://www.cnblogs.com/quchunhui/p/5783172.html`

详细用法请参考`https://gitee.com/dexterleslie/demonstration/tree/master/demo-java/demo-library/demo-logback/demo-logback-java`

引入`logback maven`依赖

```xml
<!-- logback依赖 -->
<dependency>
    <groupId>ch.qos.logback</groupId>
    <artifactId>logback-classic</artifactId>
    <version>1.4.14</version>
</dependency>
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <version>1.18.20</version>
</dependency>
```

在应用中使用`logback`

```java
package com.future.demo.logback;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

@Slf4j
public class Tests {
    @Test
    public void test() {
        log.debug("这是debug消息");
        log.error("这是error消息");
    }
}
```



## `spring-boot`项目配置`logback`

详细用法请参考`https://gitee.com/dexterleslie/demonstration/tree/master/demo-java/demo-library/demo-logback/demo-spring-boot-logback`

不需要专门引入`logback`依赖，因为`org.springframework.boot:spring-boot-starter-web`依赖已经自动引入`logback`相关依赖

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.2.7.RELEASE</version>
    </parent>

    <groupId>com.future.demo</groupId>
    <artifactId>demo-spring-boot-logback</artifactId>
    <version>1.0.0</version>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.codehaus.janino</groupId>
            <artifactId>janino</artifactId>
        </dependency>
    </dependencies>
</project>

```

在应用中使用`logback`

```java
package com.future.demo;

import com.future.demo.package1.Tester1;
import com.future.demo.package2.Tester2;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class}, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class ApplicationTests {
    final static Logger logger = LoggerFactory.getLogger(Application.class);
    final static Logger namedLogger = LoggerFactory.getLogger("namedLogger");

    @Test
    public void test() throws Exception {
        logger.debug("test method is called");

        namedLogger.debug("named logger输出日志");

        Tester1 tester = new Tester1();
        tester.method();

        Tester2 tester2 = new Tester2();
        tester2.method();
    }

}

```



## `logback`读取`spring`配置

下面的详细用法请参考`https://gitee.com/dexterleslie/demonstration/tree/master/demo-java/demo-library/demo-logback/demo-spring-boot-logback`

使用`properties resource`读取`spring`配置并注入到`logback`上下文中

```xml
<configuration debug="false">
    <property resource="application.properties"/>
    <springProfile name="!prod">
        <property resource="application-dev.properties"/>
    </springProfile>
    <springProfile name="prod">
        <property resource="application-prod.properties"/>
    </springProfile>
</configuration>
```

- `<property resource="application.properties"/>`读取`application.properties`配置文件
- `<springProfile name="!prod">`用于判断`spring profile`

使用`springProperty`读取`spring`配置并注入到`logback`上下文中

```xml
<configuration debug="false">
	<springProperty scope="context" name="logstash.url" source="spring.elk.logstash.tcp.url" defaultValue=""/>
</configuration>
```

- 读取`spring`中`spring.elk.logstash.tcp.url`配置并注入到`logback`上线文中的`logstash.url property`中

引用`logback`上下文中的`property`

```xml
<!-- 引用案例1 -->
<if condition='!property("spring.elk.logstash.tcp.url").equals("")'>
    <then>
        ...
    </then>
</if>

<!-- 引用案例2 -->
<if condition='!property("spring.application.production").equals("true")'>
    <then>
        <appender-ref ref="console"/>
    </then>
</if>

<!-- 引用案例3 -->
<if condition='!property("spring.elk.logstash.tcp.url").equals("")'>
    <then>
        <appender name="logstash" class="net.logstash.logback.appender.LogstashAccessTcpSocketAppender">
            <destination>${spring.elk.logstash.tcp.url}</destination>
            ...
        </appender>
    </then>
</if>
```



## 其他

> todo ...

```
变量定义和条件判断
https://dennis-xlc.gitbooks.io/the-logback-manual/content/en/chapter-3-configuration/configuration-file-syntax/variable-substitution.html
https://blog.csdn.net/wushengjun753/article/details/109510794

&& (logical and) and || (logical or) operators in Logback configuration (if statement)
https://stackoverflow.com/questions/41939861/logical-and-and-logical-or-operators-in-logback-configuration-if-stat

property、springProperty使用
https://blog.csdn.net/qq_34359363/article/details/104749341
1.该 <springProperty> 标签允许我们从Spring中显示属性，Environment 以便在Logback中使用。如果你想将 application.properties在回读配置中访问文件中的值，这将非常有用
2.标签的工作方式与Logback的标准 <property> 标签类似，但不是直接value 指定source属性（从Environment）指定。scope 如果需要将属性存储在local范围之外的其他位置，则可以使用该属性。如果您需要一个后备值，以防该属性未设置，则Environment可以使用该defaultValue属性。
```