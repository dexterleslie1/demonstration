# `actuator`使用



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

详细用法请参考`https://gitee.com/dexterleslie/demonstration/tree/master/demo-spring-boot/demo-spring-boot-actuator-parent/demo-spring-boot-actuator`

添加依赖

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
<!--
    暴露/actuator/prometheus端点需要此依赖，否则会报告404错误
    https://stackoverflow.com/questions/48700449/cannot-include-prometheus-metrics-in-spring-boot-2-version-2-0-0-m7
-->
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-registry-prometheus</artifactId>
</dependency>
```

`application.properties`配置

```properties
# 指定actuator端口
management.server.port=8081

# 暴露所有端点（在生产环境中应谨慎使用）
management.endpoints.web.exposure.include=*
# 支持prometheus监控指标/actuator/prometheus端点
management.metrics.export.prometheus.enabled=true

# 或者，只暴露特定的端点
# management.endpoints.web.exposure.include=health,info,metrics

# 设置Actuator的基础路径（默认为/actuator）
management.endpoints.web.base-path=/mydemo

# 配置Actuator端点的安全性（如需要身份验证）
# 这通常与Spring Security一起配置
# 例如，使用Spring Security的默认配置来保护Actuator端点
# spring.security.user.name=actuator
# spring.security.user.password=actuatorPassword
# 注意：上面的用户名和密码配置仅用于示例，实际生产环境中应使用更安全的身份验证机制
```

测试`actuator`，访问`http://localhost:8081/mydemo`、`http://localhost:8081/mydemo/prometheus`



## actuator 的端点

Spring Boot Actuator是一个用于监控和管理Spring Boot应用程序的子项目，它提供了一系列内置的端点（Endpoints），这些端点可以用于查看应用程序的状态、运行情况和运行指标。以下是一些Spring Boot Actuator的主要端点及其功能：

1. **/actuator/health**：用于查看应用程序的健康状态。它会检查各个组件的状态，如数据库连接、缓存、消息队列等，并返回状态信息，如UP（正常）、DOWN（异常）和OUT_OF_SERVICE（维护中）等。
2. **/actuator/info**：用于获取关于应用程序的定制信息，这些信息通常来源于应用程序的配置文件或构建系统。
3. **/actuator/metrics**：提供了应用程序的各项指标和性能数据，如JVM内存使用情况、GC活动、线程信息等。通过访问该端点可以查看所有可用的指标。
4. **/actuator/env**：用于查看应用程序的环境和配置信息，包括环境变量、系统属性、配置文件中的属性等。
5. **/actuator/beans**：用于查看应用程序中的所有Spring Bean，它显示了Bean的名称、类型、作用域和所属的ApplicationContext等信息。
6. **/actuator/loggers**：用于查看和管理应用程序的日志。它显示了当前应用程序中所有Logger的名称和日志级别，并允许通过发送POST请求动态修改某个Logger的日志级别。
7. **/actuator/shutdown**：用于关闭应用程序。但需要注意，这个端点在生产环境中通常需要进行权限控制，以防止误操作。要使用这个端点，需要设置`management.endpoint.shutdown.enabled`为true。

此外，Spring Boot Actuator还提供了一些其他端点，例如：

1. **/actuator/auditevents**：查看应用程序的审计事件。
2. **/actuator/threaddump**：获取应用程序的线程转储信息。
3. **/actuator/heapdump**：获取应用程序的堆转储信息。
4. **/actuator/mappings**：查看应用程序的URL映射信息。

这些端点可以通过HTTP、JMX或其他形式暴露给外部系统，便于运维人员对应用程序进行监控、诊断和管理。同时，这些端点也可以通过`application.properties`或`application.yml`文件进行配置，以控制哪些端点暴露给外部访问，以及修改端点的URL路径等。

请注意，在实际应用中，应根据项目的安全需求，对端点进行访问权限控制，避免敏感信息泄露。



### /actuator/health

`/actuator/health` 端点是 Spring Boot Actuator 提供的一个核心管理端点，用于检查应用程序的健康状况。通过访问这个端点，你可以快速了解应用程序是否运行正常，以及任何可能影响其性能或可用性的潜在问题。

**功能**

当你访问 `/actuator/health` 端点时，它会返回一个 JSON 或其他格式（取决于配置）的响应，其中包含了应用程序的整体健康状态以及各个组件（如数据库、磁盘空间等）的健康状态。这些状态通常分为以下几种：

- `UP`：表示组件运行正常。
- `DOWN`：表示组件出现故障，需要立即关注。
- `OUT_OF_SERVICE`：表示组件当前不可用，但这是由于维护或配置更改等预期原因造成的。
- `UNKNOWN`：表示无法确定组件的健康状态。

**配置**

你可以通过 `application.properties` 或 `application.yml` 配置文件来定制 `/actuator/health` 端点的行为。以下是一些常见的配置选项：

- `management.endpoint.health.show-details`：控制健康信息的详细程度。可能的值包括 `never`（不显示详细信息）、`when-authorized`（仅当请求者经过认证时显示详细信息）和 `always`（始终显示详细信息）。
- `management.endpoint.health.mapping`：允许你自定义健康状态映射。例如，你可以将特定的异常或错误条件映射为特定的健康状态。
- `management.endpoints.web.exposure.include`：确定哪些 Actuator 端点应该通过 Web 接口暴露。确保包含 `health` 以使 `/actuator/health` 可访问。

**安全性**

由于 `/actuator/health` 端点可能包含敏感信息，因此应谨慎处理其安全性。你可以使用 Spring Security 或其他安全框架来限制对该端点的访问。例如，你可以配置 Spring Security 以要求用户经过认证才能访问 `/actuator/health`。

**使用场景**

`/actuator/health` 端点在多种场景下都非常有用：

- **监控和告警**：结合监控工具（如 Prometheus、Grafana 等），你可以设置告警规则，以便在应用程序健康状态发生变化时及时收到通知。
- **自动化测试**：在持续集成/持续部署（CI/CD）管道中，你可以使用 `/actuator/health` 端点来验证部署后的应用程序是否正常运行。
- **手动检查**：开发人员和运维人员可以手动访问 `/actuator/health` 端点来快速检查应用程序的健康状况。

**示例**

假设你已经配置好了 Spring Boot 应用程序，并且 `/actuator/health` 端点可通过 Web 接口访问。你可以使用 curl 命令或浏览器来测试该端点：

```bash
curl http://localhost:8080/actuator/health
```

如果配置正确，你应该会收到一个类似于以下的 JSON 响应：

```json
{
  "status": "UP",
  "components": {
    "db": {
      "status": "UP",
      "details": {
        "database": "H2",
        "validationQuery": "isValid()"
      }
    },
    "diskSpace": {
      "status": "UP",
      "details": {
        "total": 499963170816,
        "free": 348666880000,
        "threshold": 10485760,
        "exists": true
      }
    },
    // ... 其他组件的健康信息 ...
  }
}
```

在这个示例中，`status` 字段表示应用程序的整体健康状态，而 `components` 字段则包含了各个组件的详细健康信息。



### /actuator/logfile

提醒：

- 通过配置 logging.file.name=my.log 以启用 /actuator/logfile 端点，否则访问 /actuactor/logfile 报告 404 错误。
- 端点不会返回所有日志。

访问日志断点`http://localhost:8081/mydemo/logfile`



### /actuator/shutdown

配置优雅地关闭服务，application.properties 配置如下：

```properties
# 启用/actuator/shutdown端点
management.endpoint.shutdown.enabled=true
# 优雅地关闭服务
server.shutdown=graceful
```

测试优雅地关闭

```bash
# 第一个shell，请求模拟耗时比较长的业务接口
curl -i http://localhost:8080/test1

# 第二个shell，调用/actuator/shutdown端点优雅地关闭服务
curl -i -X POST http://localhost:8081/mydemo/shutdown

# 第三个shell
curl -i http://localhost:8080/test1
```

以上测试可以看到第一个 shell 的请求正常处理，第三个 shell 被拒绝连接。



## actuator 的配置项



### management.server.port

`management.server.port` 是 Spring Boot 应用程序中的一个配置属性，它用于指定管理服务器的端口号。这个管理服务器通常用于暴露 Spring Boot Actuator 的端点，以便进行监控和管理。

**作用**

通过设置 `management.server.port` 属性，你可以将 Actuator 端点（如 `/actuator/health`、`/actuator/info` 等）与主应用程序的端口分开。这样做的好处是，你可以在不干扰主应用程序的情况下，独立地访问和管理这些端点。

**配置方式**

在 Spring Boot 的配置文件中（如 `application.properties` 或 `application.yml`），你可以这样设置 `management.server.port`：

application.properties 示例

```properties
properties复制代码

management.server.port=8081
```

application.yml 示例

```yaml
management:
  server:
    port: 8081
```

在上述配置中，管理服务器将运行在 `8081` 端口上，而主应用程序则可能运行在其他端口（如默认的 `8080` 端口）。

**注意事项**

1. **版本差异**：需要注意的是，Spring Boot 的不同版本可能对 `management.server.port` 的处理有所不同。在某些版本中，Actuator 和主应用程序可能默认就运行在不同的端口上，或者你可能需要使用其他配置属性来分离它们。因此，建议查阅你所使用的 Spring Boot 版本的官方文档以获取最准确的信息。
2. **安全性**：将管理服务器与主应用程序分开运行可以增强安全性。你可以通过防火墙或其他安全机制来限制对管理服务器的访问，从而保护敏感的监控和管理信息。
3. **访问控制**：除了端口分离外，还应考虑对 Actuator 端点进行访问控制。例如，你可以使用 Spring Security 来配置身份验证和授权，以确保只有授权用户才能访问这些端点。

**使用场景**

在生产环境中，将管理服务器与主应用程序分开运行是常见的做法。这样可以确保监控和管理操作不会干扰到应用程序的正常运行，同时也便于进行安全控制和故障排查。

总之，`management.server.port` 是一个重要的配置属性，它允许你将 Spring Boot 应用程序的管理服务器与主应用程序分开运行。在配置时，请务必考虑版本差异、安全性和访问控制等因素。



### management.endpoints.web.base-path

`management.endpoints.web.base-path` 是 Spring Boot Actuator 的一个配置属性，它用于自定义 Actuator Web 端点的基础路径。

**作用**

默认情况下，Actuator 的 Web 端点映射在 `/actuator` 路径下。例如，如果你想访问健康检查端点，其默认 URL 通常是 `/actuator/health`。通过 `management.endpoints.web.base-path` 属性，你可以更改这个基础路径，以便更好地与你的应用集成或出于安全考虑。

**配置方式**

在 Spring Boot 的配置文件中（如 `application.properties` 或 `application.yml`），你可以设置 `management.endpoints.web.base-path` 来更改 Actuator 端点的基础路径。例如：

```properties
# application.properties
management.endpoints.web.base-path=/manage
```

或者，如果你使用的是 YAML 格式的配置文件：

```yaml
# application.yml
management:
  endpoints:
    web:
      base-path: /manage
```

在上面的配置中，Actuator 端点的基础路径被更改为 `/manage`。因此，健康检查端点的 URL 将变为 `/manage/health`。

**注意事项**

- 更改基础路径后，确保你的应用能够正确访问这些端点。例如，如果你正在使用某种监控工具或脚本与 Actuator 端点进行交互，你需要更新这些工具或脚本中的 URL。
- 出于安全考虑，你可能希望将 Actuator 端点映射到一个不易被猜测的路径上，以减少潜在的安全风险。
- 在设置 `management.endpoints.web.base-path` 时，请确保该路径不会与你的应用中其他已定义的路径冲突。

**应用配置**

如果你需要在 Spring Boot 应用中修改这个配置项，你需要找到应用的配置文件，并在其中添加或修改相关配置。修改配置后，需要重启 Spring Boot 应用以使配置生效。

**示例**

假设你有一个 Spring Boot 应用，并且你已经设置了 `management.endpoints.web.base-path=/manage`。现在，如果你想通过浏览器访问健康检查端点，你可以在浏览器的地址栏中输入类似以下的 URL：

```
复制代码

http://localhost:8080/manage/health
```

（其中 `8080` 是你的应用运行的端口号，可能因你的配置而异。）

总之，`management.endpoints.web.base-path` 是一个有用的配置属性，它允许你自定义 Actuator Web 端点的基础路径，以便更好地与你的应用集成或出于安全考虑。



### management.endpoints.web.exposure.include

`management.endpoints.web.exposure.include` 是 Spring Boot Actuator 的一个关键配置项，它用于指定哪些 Actuator 端点应该通过 Web 接口暴露出来。这个属性允许开发者精确地控制外部访问的端点，从而提高应用的安全性。

**配置方式**

在 Spring Boot 的配置文件中（如 `application.properties` 或 `application.yml`），你可以设置 `management.endpoints.web.exposure.include` 来列出希望暴露的端点。例如：

```properties
# application.properties
management.endpoints.web.exposure.include=health,info,beans
```

或者，如果你使用的是 YAML 格式的配置文件：

```yaml
# application.yml
management:
  endpoints:
    web:
      exposure:
        include: health,info,beans
```

在上面的配置中，`health`、`info` 和 `beans` 端点被指定为通过 Web 接口暴露。

**通配符使用**

`management.endpoints.web.exposure.include` 属性支持使用星号（`*`）作为通配符，以表示暴露所有端点。例如：

```yaml
# 暴露所有端点
management:
  endpoints:
    web:
      exposure:
        include: "*"
```

然而，需要注意的是，如果配置文件中同时指定了 `include` 和 `exclude` 属性，`exclude` 属性的优先级将高于 `include`。这意味着即使你使用了 `*` 来暴露所有端点，但如果 `exclude` 属性中列出了某些端点，这些端点仍然不会被暴露。

**安全性考虑**

由于 Actuator 端点可能包含敏感信息（如应用配置、健康状态等），因此在使用 `management.endpoints.web.exposure.include` 时应谨慎考虑。只暴露必要的端点，并确保这些端点的访问受到适当的保护（如使用 Spring Security 进行身份验证和授权）。

**示例**

假设你有一个 Spring Boot 应用，并且你已经设置了 `management.endpoints.web.exposure.include=health,info`。现在，如果你想通过浏览器访问这些端点，你可以在浏览器的地址栏中输入类似以下的 URL：

```
http://localhost:8080/actuator/health
http://localhost:8080/actuator/info
```

（其中 `8080` 是你的应用运行的端口号，可能因你的配置而异。）

如果你希望暴露所有端点（除了某些特定端点外），你可以使用 `*` 作为通配符，并在 `exclude` 属性中列出不希望暴露的端点。例如：

```yaml
# 暴露所有端点，但排除 env 和 beans
management:
  endpoints:
    web:
      exposure:
        include: "*"
        exclude: env,beans
```

总之，`management.endpoints.web.exposure.include` 是一个重要的配置属性，它允许你精确地控制哪些 Actuator 端点应该通过 Web 接口暴露出来。在配置时，请务必考虑应用的安全性和实际需求。



### management.metrics.export.prometheus.enabled

`management.metrics.export.prometheus.enabled` 是 Spring Boot 应用中用于配置 Prometheus 监控的一个关键属性。它决定了 Spring Boot Actuator 是否会将应用的度量指标（metrics）以 Prometheus 的格式导出。

**作用**

当 `management.metrics.export.prometheus.enabled` 设置为 `true` 时，Spring Boot 应用会启动一个 HTTP 服务器，并向 `/actuator/prometheus` 路径提供可供 Prometheus 采集的指标数据。这样，Prometheus 服务器就可以定期从这个端点抓取数据，进而进行监控和告警。

**配置方式**

在 Spring Boot 的配置文件中（如 `application.properties` 或 `application.yml`），你可以设置 `management.metrics.export.prometheus.enabled` 来启用或禁用 Prometheus 格式的度量指标导出。例如：

```properties
# application.properties
management.metrics.export.prometheus.enabled=true
```

或者，如果你使用的是 YAML 格式的配置文件：

```yaml
# application.yml
management:
  metrics:
    export:
      prometheus:
        enabled: true
```

**注意事项**

1. **安全性**：由于 `/actuator/prometheus` 端点会暴露应用的度量指标，因此应确保这个端点的访问受到适当的保护，以避免敏感信息泄露。你可以使用 Spring Security 来配置访问控制。
2. **配置依赖**：为了使 Prometheus 能够正确抓取数据，你还需要在项目中添加相应的依赖，如 `micrometer-registry-prometheus`。这个依赖会将 Spring Boot 的度量指标格式化为 Prometheus 可读的格式。
3. **Prometheus 配置**：在 Prometheus 的配置文件中（如 `prometheus.yml`），你需要添加一个 job 来监控 Spring Boot 应用。这个 job 会指定从哪个端点抓取数据（即 `/actuator/prometheus`），以及被抓取数据的频率等。

**示例**

假设你已经配置好了 Spring Boot 应用和 Prometheus 服务器，现在你可以通过浏览器访问类似以下的 URL 来查看 Prometheus 格式的度量指标数据：

```
复制代码

http://localhost:8080/actuator/prometheus
```

（其中 `8080` 是你的 Spring Boot 应用运行的端口号，可能因你的配置而异。）

如果一切正常，你应该会看到类似以下的输出，其中包含了你应用的度量指标数据：

```
# HELP application_cpus_usage_percent The recent CPU usage for the Java Virtual Machine process
# TYPE application_cpus_usage_percent gauge
application_cpus_usage_percent{app="your-app-name",...} 12.34
# ... 其他度量指标数据 ...
```

总之，`management.metrics.export.prometheus.enabled` 是一个重要的配置属性，它允许你将 Spring Boot 应用的度量指标以 Prometheus 的格式导出，从而实现对应用的监控和告警。在配置时，请务必考虑应用的安全性和实际需求。



### management.endpoint.health.show-details

`management.endpoint.health.show-details` 是 Spring Boot Actuator 中的一个配置属性，它用于控制健康端点（health endpoint）响应中详细信息的显示程度。以下是对该属性的详细解释：

**作用**

Spring Boot Actuator 提供了健康端点，用于检查应用程序的健康状况，并返回相应的状态信息。通过配置 `management.endpoint.health.show-details` 属性，可以控制这些状态信息中详细内容的可见性。

**可能的值及其含义**

- `never`：不显示详细信息，只返回状态码（如 `UP`、`DOWN` 等）。这是默认值。
- `when-authorized`：仅当请求用户经过认证且拥有适当权限时，才显示详细信息。这意味着，如果应用程序配置了安全机制（如 Spring Security），则只有经过认证的用户才能看到详细的健康信息。
- `always`：无论请求用户是否经过认证或拥有权限，都显示详细信息。

**配置方式**

在 Spring Boot 的配置文件中（如 `application.properties` 或 `application.yml`），你可以这样设置 `management.endpoint.health.show-details`：

application.properties 示例

```properties
properties复制代码

management.endpoint.health.show-details=always
```

application.yml 示例

```yaml
management:
  endpoint:
    health:
      show-details: always
```

**注意事项**

- **安全性**：将 `management.endpoint.health.show-details` 设置为 `always` 可能会暴露敏感信息，因此应谨慎使用。如果应用程序面向公众开放，建议将其设置为 `never` 或 `when-authorized`，并结合安全机制（如 Spring Security）来保护健康端点。
- **版本兼容性**：不同版本的 Spring Boot 可能对 `management.endpoint.health.show-details` 属性的处理有所不同。因此，在配置时，请务必参考你所使用的 Spring Boot 版本的官方文档。

**示例场景**

假设你有一个 Spring Boot 应用程序，并且你想配置健康端点以始终显示详细信息。你可以在 `application.yml` 中进行如下配置：

```yaml
management:
  endpoints:
    web:
      base-path: /actuator
      exposure:
        include: health,info
    health:
      show-details: always
```

在这个示例中，健康端点被配置为始终显示详细信息，并且可以通过 `/actuator/health` 路径进行访问。同时，还配置了 Actuator 端点的基础路径为 `/actuator`，并暴露了 `health` 和 `info` 端点。

总之，`management.endpoint.health.show-details` 是一个重要的配置属性，它允许你控制 Spring Boot 应用程序健康端点响应中详细信息的显示程度。在配置时，请务必考虑安全性、版本兼容性和实际需求。



### management.endpoint.shutdown.enabled

为 true 的时候启用/actuator/shutdown端点



## SpringBoot Admin

SpringBoot Admin（SBA）是一个开源的社区项目，主要用于管理和监控Spring Boot应用程序。以下是对SpringBoot Admin的详细介绍：

**一、主要功能**

SpringBoot Admin提供了一个基于Web的用户界面，用于展示和管理Spring Boot应用程序的运行状态。它利用Spring Boot Actuator提供的端点信息，以图形化的方式展示应用的健康状态、指标、配置等。具体来说，其功能包括但不限于：

1. **展示被监控Spring Boot项目的基本信息**：如应用名称、版本、启动时间等。
2. **详细的健康信息**：展示应用的健康状态，包括各个组件的健康状况。
3. **内存和JVM信息**：展示应用的内存使用情况、JVM版本、垃圾回收信息等。
4. **垃圾回收信息**：提供垃圾回收的详情，包括回收次数和花费时间。
5. **配置信息**：展示应用的各种配置信息，如数据源、缓存列表和命中率等。
6. **日志查看和配置**：可以直接查看和配置Spring Boot项目中的日志级别。
7. **线程转储和HTTP traces**：提供线程转储信息，以及HTTP请求的跟踪信息。
8. **查看计划任务**：展示Spring Boot中的定时任务信息。
9. **状态更改通知**：支持通过电子邮件、Slack等方式发送状态变化通知。
10. **事件日志**：记录应用状态变化的事件日志，方便追踪问题。

**二、架构组成**

SpringBoot Admin主要由服务端（server）和客户端（client）两部分组成：

1. **服务端**：提供用户界面，展示和交互Spring Boot Actuators。服务端可以作为servlet或webflux应用程序运行，需要添加相应的Spring Boot Starter。
2. **客户端**：用于在服务端注册，并允许访问Actuator端点。每个想要注册的应用都需要包含Spring Boot Admin Client。

**三、使用步骤**

1. **创建服务端**：
   - 创建一个Spring Boot工程，并添加`spring-boot-starter-web`和`spring-boot-admin-starter-server`依赖。
   - 在启动类上添加`@EnableAdminServer`注解，开启监控服务。
   - 配置服务端端口号，避免与其他Spring Boot项目冲突。
2. **创建客户端**：
   - 创建一个普通的Spring Boot项目，并添加`spring-boot-admin-starter-client`依赖。
   - 在`application.properties`或`application.yml`文件中配置SBA服务器端地址。
   - （可选）为了查看更多的监控项，需要在客户端项目中添加`spring-boot-starter-actuator`依赖，并配置开放所有监控项。
3. **启动和访问**：
   - 启动服务端和客户端项目。
   - 访问服务端的地址，即可看到被监控的Spring Boot项目列表和详细信息。

**四、安全性设置**

在生产环境中，建议配置Spring Security来保护SpringBoot Admin的管理界面，防止未授权访问。可以通过添加`spring-boot-starter-security`依赖，并配置用户名和密码来实现。

**五、集成与扩展**

1. **与Spring Cloud集成**：SpringBoot Admin可以与Spring Cloud集成，用于监控和管理分布式系统中的各个服务。
2. **集成通知系统**：可以通过配置SpringBoot Admin的通知功能，将监控信息发送到Slack、Email等通知系统，以便及时处理异常情况。
3. **自定义监控指标**：根据业务需求，可以自定义监控指标，并通过SpringBoot Admin进行展示。

综上所述，SpringBoot Admin是一个功能强大的Spring Boot应用程序管理和监控工具，它提供了丰富的监控信息和便捷的管理界面，有助于开发人员更好地了解应用的运行状态并及时处理异常情况。



### 配置服务端

maven 配置新增 spring-boot-admin-starter-server 依赖：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
<dependency>
    <groupId>de.codecentric</groupId>
    <artifactId>spring-boot-admin-starter-server</artifactId>
    <version>2.3.1</version>
</dependency>
```

Application 添加 @EnableAdminServer 注解启用 Admin 服务端

```java
@SpringBootApplication
@EnableAdminServer
public class DemoSpringBootAdminServerApplication {
```

访问 Admin 服务 `http://localhost:8082/`



### 配置客户端

注意：在 actuator 所在的应用中配置 Admin 客户端，本示例在 demo-spring-boot-actuator 示例中配置 Admin 客户端。

maven 配置新增 spring-boot-admin-starter-client 依赖：

```xml
<!-- SpringBoot Admin 客户端依赖 -->
<dependency>
    <groupId>de.codecentric</groupId>
    <artifactId>spring-boot-admin-starter-client</artifactId>
    <version>2.3.1</version>
</dependency>
```

application.properties 配置 Admin 客户端如下：

```properties
# 配置 SpringBoot Admin 客户端
spring.boot.admin.client.url=http://localhost:8082
# 指定在注册到 Spring Boot Admin 服务端时，是否优先使用 IP 地址而不是主机名（hostname）
spring.boot.admin.client.instance.prefer-ip=true
```

访问 Admin 服务 `http://localhost:8082/`，此时能够看到 Admin 客户端成功注册到 Admin 服务端中。



### 安全防护配置

>下载对应的版本的 SpringBoot Admin 源代码再打开文档查看安全防护配置。

通过整合 Spring Security 实现安全防护功能。

maven 添加 Spring Security 依赖

```xml
<!-- SpringBoot Admin 安全防护配置依赖于 Spring Security -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

新增 Spring Security 配置类 SecuritySecureConfig

```java
package com.future.demo.config;

import de.codecentric.boot.admin.server.config.AdminServerProperties;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.util.UUID;

// SpringBoot Admin 安全防护的 Spring Security 配置
@Configuration(proxyBeanMethods = false)
public class SecuritySecureConfig extends WebSecurityConfigurerAdapter {

    private final AdminServerProperties adminServer;
    private final SecurityProperties security;

    public SecuritySecureConfig(AdminServerProperties adminServer, SecurityProperties security) {
        this.adminServer = adminServer;
        this.security = security;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        SavedRequestAwareAuthenticationSuccessHandler successHandler = new SavedRequestAwareAuthenticationSuccessHandler();
        successHandler.setTargetUrlParameter("redirectTo");
        successHandler.setDefaultTargetUrl(this.adminServer.path("/"));

        http.authorizeRequests(
                        (authorizeRequests) -> authorizeRequests.antMatchers(this.adminServer.path("/assets/**")).permitAll() // <1>
                                .antMatchers(this.adminServer.path("/login")).permitAll().anyRequest().authenticated() // <2>
                ).formLogin(
                        (formLogin) -> formLogin.loginPage(this.adminServer.path("/login")).successHandler(successHandler).and() // <3>
                ).logout((logout) -> logout.logoutUrl(this.adminServer.path("/logout"))).httpBasic(Customizer.withDefaults()) // <4>
                .csrf((csrf) -> csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()) // <5>
                        .ignoringRequestMatchers(
                                new AntPathRequestMatcher(this.adminServer.path("/instances"),
                                        HttpMethod.POST.toString()), // <6>
                                new AntPathRequestMatcher(this.adminServer.path("/instances/*"),
                                        HttpMethod.DELETE.toString()), // <6>
                                new AntPathRequestMatcher(this.adminServer.path("/actuator/**")) // <7>
                        ))
                .rememberMe((rememberMe) -> rememberMe.key(UUID.randomUUID().toString()).tokenValiditySeconds(1209600));
    }

    // Required to provide UserDetailsService for "remember functionality"
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication().withUser(this.security.getUser().getName())
                .password("{noop}" + this.security.getUser().getPassword()).roles("USER");
    }

}
```

application.properties 配置 Spring Security 帐号和密码

```properties
# SpringBoot Admin 安全防护的 Spring Security 配置
spring.security.user.name=root
spring.security.user.password=123456
```

SpringBoot Admin 客户端配置 SpringBoot Admin 服务器的帐号密码，否则 SpringBoot Admin 客户端无法注册到 SpringBoot Admin 服务器，本示例在 demo-spring-boot-actuator 模块的 application.properties 配置文件中添加以下内容：

```properties
# 配置SpringBoot Admin 服务端帐号密码
spring.boot.admin.client.username=root
spring.boot.admin.client.password=123456
```



### 通过注册中心集成客户端

提示：不需要配置 SpringBoot Admin 客户端 maven 依赖和客户端信息。

SpringBoot Admin 服务端注册到注册中心后自动发现并配置 SpringBoot Admin 客户端进行监控。

本示例使用 Eureka 作为注册中心自动集成 SpringBoot Admin 客户端。

先运行一个 Eureka 注册中心

配置 demo-spring-boot-admin-server（SpringBoot Admin 服务端）注册到 Eureka 注册中心

添加 maven Eureka 依赖

```xml
<!-- 向eureka注册自己需要引入下面的spring-cloud-starter-netflix-eureka-client依赖 -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency>
```

配置 Eureka 注册中心信息并忽略不监控自己

```properties
# eureka注册中心注册地址
eureka.client.serviceUrl.defaultZone=http://localhost:9999/eureka/
# eureka实例面板显示实例的主机ip
eureka.instance.prefer-ip-address=true
# 忽略自身 SpringBoot Admin 服务器监控
spring.boot.admin.discovery.ignored-services=demo-spring-boot-admin-server
```

配置 demo-spring-boot-actuator-discovery-client（SpringBoot Admin 客户端）注册到 Eureka 注册中心后 SpringBoot Admin 服务器端会自动发现并配置 SpringBoot Admin 客户端

配置 SpringBoot Admin 客户端 actuator 配置，maven 中添加 actuator 依赖

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

```properties
# 暴露所有actuator所有端点
management.endpoints.web.exposure.include=*
```

配置客户端注册到 Eureka 注册中心

```xml
<!-- 向eureka注册自己需要引入下面的spring-cloud-starter-netflix-eureka-client依赖 -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency>
```

```properties
# eureka注册中心注册地址
eureka.client.serviceUrl.defaultZone=http://localhost:9999/eureka/
# eureka实例面板显示实例的主机ip
eureka.instance.prefer-ip-address=true
```

访问 SpringBoot Admin 服务器`http://localhost:8082/`，此时发现 demo-spring-boot-actuator-discovery-client 会自动注册到 SpringBoot Admin 服务中。



### 邮件通知配置

SpringBoot Admin 服务端 maven 添加 mail 依赖

```xml
<!-- 邮件通知依赖 -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-mail</artifactId>
</dependency>
```

application.properties 配置 smtp

```properties
# 邮件通知
spring.mail.protocol=smtp
spring.mail.host=smtp.qq.com
spring.mail.port=465
spring.mail.username=xxx@qq.com
spring.mail.password=xxx
# 配置smtp ssl
spring.mail.properties.mail.smtp.ssl.enable=true
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
spring.mail.properties.mail.smtp.starttls.require=true
spring.boot.admin.notify.mail.from=xxx@qq.com
spring.boot.admin.notify.mail.to=xxx@qq.com
```

测试关闭其中一个 SpringBoot Admin 客户端，稍等一会儿后会收到关于服务下线的邮件通知。



## actuator 定制端点



### 定制 health 端点

```java
@Component
public class CustomHealthIndicator extends AbstractHealthIndicator {
    @Override
    protected void doHealthCheck(Health.Builder builder) throws Exception {
        // 构建up状态
        builder.up().withDetail("detail1", "Detail value 1")
                .withDetail("detail2", "Detail value 2");

        // 构建down状态
        /*builder.down().withDetail("error", "Error value")
                .withException(new Exception("测试异常"));*/
    }
}
```

启动应用后再次访问`http://localhost:8081/mydemo/health`端点查看到新增了名为 custom 的组件。



### 定制 info 端点

方法1：使用 application.properties 配置的方式定制 info 端点

```properties
# 使用配置的方式定制 info 端点
info.app.name=Spring Sample Application
info.app.description=This is my first spring boot application
info.app.version=1.0.0
info.java-vendor = ${java.specification.vendor}
info.customize.info1=Information 1
info.customize.info2=Information 2
```



方法2：派生于 InfoContributor 定制 info 端点

```java
package com.future.demo;

import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Dexterleslie.Chan
 */
@Component
public class CustomizeInfoContributor implements InfoContributor {
    @Override
    public void contribute(Info.Builder builder) {
        Map<String, Object> info = new HashMap<>();
        info.put("info1", "information one");
        info.put("info2", "information two");
        builder.withDetails(info);
    }
}

```

启动应用后再次访问`http://localhost:8081/mydemo/info`端点查看到新增的 info 信息了。



### 定制 metrics 端点

提示：自定义指标是通过 micrometer 的接口添加自定义指标。

新增自定义指标

```java
// 用于协助/actuator/logfile测试产生日志
// 用于协助测试自定义指标开发
@GetMapping("/")
public String index() {
    log.info("Hello World!");

    // 自定义指标测试
    Metrics.counter("demo.index.counter", "uri", "/").increment();
    Timer timer = Metrics.timer("demo.index.timer", "uri", "/");
    timer.record(() -> {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    });

    return "Hello World";
}
```

访问接口`http://localhost:8080/`以产生自定义指标数据

查看`http://localhost:8081/mydemo/metrics`端点新增了 demo.index.counter 和 demo.index.timer 自定义指标。



### 定制 Endpoint

新增自定义 Endpoint

```java
package com.future.demo;

import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.annotation.WriteOperation;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

// 自定义endpoint
@Component
@Endpoint(id = "custom")
public class CustomEndpoint {
    private final Map<String, Object> map = new HashMap<String, Object>() {{
        put("name", "custom");
        put("age", 18);
    }};
    @ReadOperation
    public Map<String, Object> get() {
        return map;
    }

    @WriteOperation
    public void post(String key, Object value) {
        map.put(key, value);
    }
}
```

访问`http://localhost:8081/mydemo`查看新增了 /mydemo/custom 端点

测试 /mydemo/custom 端点读取数据

```bash
curl -i -X GET http://localhost:8081/mydemo/custom
```

