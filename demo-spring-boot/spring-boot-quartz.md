# `spring-boot quartz`使用

## `spring-boot quartz`集成

详细配置请参考 [示例](https://gitee.com/dexterleslie/demonstration/tree/master/demo-spring-boot/demo-spring-boot-quartz)

`maven`设置如下：

```xml
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
	<modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.2.7.RELEASE</version>
    </parent>

	<groupId>com.future.demo.java</groupId>
    <artifactId>demo-spring-boot-quartz</artifactId>
	<version>1.0.0</version>

	<dependencies>
        <!-- 配置quartz依赖 -->
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-quartz</artifactId>
		</dependency>
	</dependencies>
</project>

```

`spring-boot`配置启用`quartz`

```java
package com.future.demo.quartz;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
@EnableScheduling
public class ConfigQuartz {
    // 配置spring scheduling核心执行线程，spring默认执行线程数为1
    // https://stackoverflow.com/questions/29796651/what-is-the-default-scheduler-pool-size-in-spring-boot
    @Bean
    public TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(5);
        return scheduler;
    }
}

```



## 配置`spring scheduling`执行线程数

配置`spring scheduling`核心执行线程，`spring`默认执行线程数为`1`

```java
@Configuration
@EnableScheduling
public class ConfigQuartz {
    // 配置spring scheduling核心执行线程，spring默认执行线程数为1
    // https://stackoverflow.com/questions/29796651/what-is-the-default-scheduler-pool-size-in-spring-boot
    @Bean
    public TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(5);
        return scheduler;
    }
}
```



## `quartz`表达式

>[Cron Trigger Tutorial](https://www.quartz-scheduler.org/documentation/quartz-2.3.0/tutorials/crontrigger.html)

详细用法请参考 [示例](https://gitee.com/dexterleslie/demonstration/tree/master/demo-spring-boot/demo-spring-boot-quartz)

- 每秒执行一次

  ```java
  // 每秒触发一次
  @Scheduled(cron = "0/1 * * * * ?")
  public void doCronbEverySecond() {
      Date date = new Date();
      System.out.println("每秒触发一次：" + date);
  }
  ```

- 每小时的每分钟的第1秒触发一次

  ```java
  // 每小时的每分钟的第1秒触发一次
  @Scheduled(cron = "1 * * * * ?")
  public void triggerOnceAtFirstSecondEveryMinute() {
      Date date = new Date();
      System.out.println("每小时的每分钟的第1秒触发一次：" + date);
  }
  ```

- 每隔5秒触发一次，0/5 表示从0秒开始

  ```java
  // 每隔5秒触发一次，0/5 表示从0秒开始
  @Scheduled(cron = "0/5 * * * * ?")
  public void cronbTask() {
      Date date = new Date();
      System.out.println("每5秒触发一次：" + date);
  }
  ```

- 每天13:06:03触发一次

  ```java
  // 每天13:06:03触发一次
  @Scheduled(cron = "3 6 13 * * ?")
  public void triggerTest1() {
      Date date = new Date();
      System.out.println("每天13:06:03触发一次：" + date);
  }
  ```

- 每天13点每2分钟触发

  ```java
  // 每天13点每2分钟触发
  @Scheduled(cron = "0 0/2 13 * * ?")
  public void doCronbStartAt10Every2Minute() {
      Date date = new Date();
      System.out.println("每天13点每2分钟触发：" + date);
  }
  ```

- 每分钟触发一次

  ```java
  // 每分钟触发一次
  @Scheduled(cron = "0 0/1 * * * ?")
  public void doCronbEveryMinute() {
      Date date = new Date();
      System.out.println("每分钟触发一次：" + date);
  }
  ```

  