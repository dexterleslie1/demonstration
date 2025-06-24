# `prometheus`监控`spring`



## `Prometheus+SpringBoot Actuator` 监控

> 注意：`dashboard 12464`不兼容`spring-boot-v2.0.3.RELEASE`，但兼容`spring-boot-v2.2.13.RELEASE`和`spring-boot-v2.7.2`。
>
> 详细用法请参考本站 [示例1](https://gitee.com/dexterleslie/demonstration/tree/main/demo-prometheus/demo-spring-boot-actuator)、[示例2](https://gitee.com/dexterleslie/demonstration/tree/main/demo-prometheus/demo-docker-compose-prometheus)

配置 `SpringBoot Actuator`：

- `POM` 配置：

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

- `application.properties` 配置启用 `prometheus` 端点：

  ```properties
  # 指定actuator端口
  management.server.port=8081
  management.endpoints.web.exposure.include=*
  # 支持prometheus监控指标/actuator/prometheus端点
  management.metrics.export.prometheus.enabled=true
  ```

配置 `Prometheus`：

- `prometheus.yml` 如下配置：

  ```yaml
  # 搜刮配置
  scrape_configs:
    - job_name: 'spring-boot actuator'
      scrape_interval: 15s
      metrics_path: '/actuator/prometheus'
      static_configs:
      - targets: ['192.168.235.128:8081']
        labels:
          instance: 'spring-boot actuator'
  
  ```

- 登录 `prometheus http://localhost:3000` 导入 `12464` 模板。





