# `spring-boot actuator`使用



## 什么是`actuator`？

Spring Boot Actuator是一个用于监控和管理Spring Boot应用的框架。以下是对Spring Boot Actuator的详细介绍：

**一、Spring Boot Actuator的主要功能**

Spring Boot Actuator模块提供了生产级别的功能，如健康检查、审计、指标收集、HTTP跟踪等，帮助开发者监控和管理Spring Boot应用。这个模块是一个采集应用内部信息并将其暴露给外部的模块，上述功能都可以通过HTTP和JMX进行访问。

**二、Spring Boot Actuator的端点（Endpoints）**

Spring Boot Actuator提供了许多内置的端点，允许开发者访问应用程序的运行时信息。以下是一些常用的端点及其功能：

1. **/actuator/health**：显示应用程序的健康状况。通过检查数据库、缓存、消息代理等资源的状态，确定应用程序是否正常运行。
2. **/actuator/info**：显示应用程序的相关信息，如版本、构建时间等。
3. **/actuator/metrics**：显示各种JVM指标、计数器和度量数据的详细信息。Spring Boot提供了一些默认的度量指标，如系统CPU使用率、内存使用情况、HTTP请求延迟等。
4. **/actuator/beans**：显示Spring应用程序中所有Bean的完整列表及其相关信息。
5. **/actuator/env**：显示应用程序当前环境变量和属性的详细信息。
6. **/actuator/mappings**：显示所有@RequestMapping路径的映射信息。
7. **/actuator/shutdown**：允许开发者优雅地关闭Spring Boot应用程序（需要额外配置）。

此外，Spring Boot Actuator还支持自定义端点，开发者可以根据自己的需求创建新的端点来暴露特定的信息或执行特定的操作。

**三、Spring Boot Actuator的配置**

Spring Boot Actuator的配置主要通过`application.properties`或`application.yml`文件进行。以下是一些常用的配置项：

1. **management.endpoints.web.exposure.include**：指定哪些端点应该被暴露出来。默认情况下，它的值是`*`，表示所有的端点都会被暴露出来。如果只想暴露特定的端点，可以将这个配置项的值改为这些端点的名称，用逗号分隔。
2. **management.endpoints.web.exposure.exclude**：指定哪些端点不应该被暴露出来。默认情况下，它的值是空字符串，表示没有端点被排除。如果想关闭某个端点，可以将这个配置项的值改为这个端点的名称。
3. **management.endpoints.web.base-path**：指定所有端点的基础路径。默认情况下，它的值是`/actuator`，表示所有的端点的路径都会以`/actuator`开始。
4. **management.endpoints.web.path-mapping**：指定端点的路径映射。如果想改变某个端点的路径，可以将这个配置项的值改为端点的名称和新的路径，用冒号分隔。

**四、Spring Boot Actuator的安全性**

由于Spring Boot Actuator暴露的端点包含了应用程序的敏感信息，因此必须注意其安全性。以下是一些提高Spring Boot Actuator安全性的建议：

1. **使用Spring Security保护端点**：通过添加Spring Security依赖并配置相应的安全规则，可以保护Spring Boot Actuator的端点免受未经授权的访问。
2. **限制端点的访问**：通过配置`management.endpoints.web.exposure.include`和`management.endpoints.web.exposure.exclude`等属性，可以限制哪些端点被暴露出来，从而降低安全风险。
3. **使用HTTPS**：通过配置SSL/TLS，可以使用HTTPS协议来加密Spring Boot Actuator端点与客户端之间的通信，防止敏感信息在传输过程中被窃取或篡改。

**五、Spring Boot Actuator与外部监控系统的集成**

Spring Boot Actuator可以与各种外部监控系统集成，如Prometheus、Grafana、Micrometer等。这些外部监控系统提供了出色的仪表板、图形、分析和警报功能，可以帮助开发者通过一个统一友好的界面来监视和管理应用程序。

Spring Boot Actuator使用Micrometer与这些外部应用程序监视系统集成。Micrometer为Java平台上的性能数据收集提供了一个通用的API，应用程序只需要使用Micrometer的通用API来收集性能指标即可。Micrometer会负责完成与不同监控系统的适配工作，使得切换监控系统变得很容易。

综上所述，Spring Boot Actuator是一个功能强大的框架，可以帮助开发者监控和管理Spring Boot应用。通过合理配置和使用Spring Boot Actuator的端点和功能，可以提高应用程序的可靠性和安全性。



## 启用`actuator`

详细用法请参考`https://gitee.com/dexterleslie/demonstration/tree/master/demo-spring-boot/demo-spring-boot-actuator`

添加依赖

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

`application.properties`配置

```properties
# 指定actuator端口
management.server.port=8081

# 暴露所有端点（在生产环境中应谨慎使用）
management.endpoints.web.exposure.include=*

# 或者，只暴露特定的端点
# management.endpoints.web.exposure.include=health,info,metrics

# 设置Actuator的基础路径（默认为/actuator）
management.endpoints.web.base-path=/actuator

# 配置Actuator端点的安全性（如需要身份验证）
# 这通常与Spring Security一起配置
# 例如，使用Spring Security的默认配置来保护Actuator端点
# spring.security.user.name=actuator
# spring.security.user.password=actuatorPassword
# 注意：上面的用户名和密码配置仅用于示例，实际生产环境中应使用更安全的身份验证机制
```

测试`actuator`，访问`http://localhost:8081/actuator/info`