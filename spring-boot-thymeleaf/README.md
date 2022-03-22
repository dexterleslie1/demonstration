# 演示thymeleaf使用

## 配置修改html和javascript thymeleaf自动reload，不需要重启springboot

> 参考： [springboot集成thymeleaf(不重启刷新html)](https://blog.csdn.net/KevinDai007/article/details/79479847)

- 步骤1、application.properties添加
```text
spring.thymeleaf.cache=false
```

- 步骤2、pom.xml添加spring-boot-devtools依赖
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-devtools</artifactId>
    <optional>true</optional>
</dependency>
```

- 步骤3、启用 Settings->Build, Execution, Deployment->Compiler->Build project automatically

- 步骤4、选择ctrl+option+shift+/->Registry后，启用compiler.automake.allow.when.app.running