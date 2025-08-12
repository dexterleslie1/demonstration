# 自定义`exporter`



## 使用`simpleclient`

>注意：
>
>- `prometheus`被设计为拉取指标模式，`simpleclient`不存在主动连接`prometheus`服务器推送指标数据的概念。
>- `SpringBoot` 应用自定义指标的主流解决方案为 `Micrometer`，所以不采用 `simpleclient` 自定义指标。
>
>详细用法请参考本站示例 [`demo-simpleclient`](https://gitee.com/dexterleslie/demonstration/tree/main/demo-prometheus/demo-prometheus-simpleclient)编写自定`exporter`，使用 `simpleclient` 编写自定义`exporter（又称为metrics endpoint）`。

Prometheus `simpleclient` 是 Prometheus 官方提供的 Java 客户端库，它允许 Java 应用程序创建和暴露 metrics，这些 metrics 可以被 Prometheus 服务器拉取以进行监控和告警。`simpleclient` 提供了一组简单的 API，用于定义和注册计数器（Counter）、直方图（Histogram）、摘要（Summary）和仪表（Gauge）等类型的 metrics。

使用 `simpleclient`，Java 应用程序可以轻松地暴露其内部状态和运行指标，例如请求数、处理时间、内存使用情况等。这些指标可以通过 HTTP 接口（通常是 `/metrics` 端点）进行访问，Prometheus 服务器会定期拉取这些指标数据，并存储在本地以供查询和可视化。

`simpleclient` 库还包含一些额外的模块，用于与其他 Java 组件集成和简化常见任务的执行。例如，`simpleclient_hotspot` 模块可以自动收集和暴露 JVM 的热点指标（如类加载、垃圾回收等），而 `simpleclient_servlet` 模块可以与 Java Servlet 容器（如 Tomcat、Jetty）集成，以在 Servlet 应用程序中轻松暴露 metrics。

虽然 `simpleclient` 提供了一个基础的 Prometheus 客户端实现，但在实际应用中，特别是在使用 Spring Boot 或其他现代 Java 框架时，更常见的做法是使用 Micrometer 这样的库。Micrometer 是一个通用的度量库，它提供了对多种监控系统的支持，包括 Prometheus，并提供了与 Spring Boot 等框架的集成，使得集成和配置变得更加简单和直观。

总之，Prometheus `simpleclient` 是 Prometheus 官方提供的 Java 客户端库，用于在 Java 应用程序中创建和暴露 metrics，以便 Prometheus 服务器进行监控和告警。然而，在实际应用中，使用 Micrometer 这样的库可能更为常见和方便。

配置 `simpleclient` 客户端：

- `POM` 引用`simpleclient`

  ```xml
  <!-- simpleclient_servlet会自动引入simpleclient -->
  <dependency>
      <groupId>io.prometheus</groupId>
      <artifactId>simpleclient_servlet</artifactId>
      <version>0.16.0</version>
  </dependency>
  ```

- `SpringBoot`项目`SimpleClientConfig.java`配置如下，例子中创建`Counter`、`Gauge`、`Histogram`、`Summary`指标分别演示其各自的用法：

  ```java
  @Configuration
  @Getter
  public class SimpleClientConfig {
  
      // 创建一个Counter
      Counter myCounter = Counter.build()
              .name("my_custom_counter")
              .help("这是自定义counter指标类型")
              // 标签的分类，例如：此例子中标签的分类为实例
              .labelNames("instance")
              .register();
  
      // 创建一个Gauge
      Gauge myGauge = Gauge.build()
              .name("my_custom_gauge")
              .help("这是自定义gauge指标类型")
              .register();
  
      // 模拟http请求响应时间
      Histogram requestDurationHistogram = Histogram.build()
              .name("http_request_duration_milliseconds")
              .help("Request latency in milliseconds.")
              .labelNames("method", "path")
              // 桶中的区间的分布如下 10-100（单位是毫秒）、100-200、...
              .buckets(10, 100, 200, 300, 500, 1000, 5000, 10000, 30000)
              .register();
  
      // 模拟http请求响应时间的分位分布
      Summary summary = Summary.build()
              .name("http_request_duration_milliseconds_summary")
              .help("HTTP request latencies in milliseconds.")
              .quantile(0.1, 0.05) // 添加50%分位数，±5%容忍度
              .quantile(0.5, 0.05) // 添加50%分位数，±5%容忍度
              .quantile(0.9, 0.01) // 添加90%分位数，±1%容忍度
              .quantile(0.99, 0.001) // 添加99%分位数，±0.1%容忍度
              .register();
  
      // 在某处（如Controller或服务中）增加Counter的值
      // ...
  
      // 注册MetricsServlet以暴露metrics
      @Bean
      public ServletRegistrationBean<Servlet> servletRegistrationBean() {
          return new ServletRegistrationBean<>(new MyMetricsServlet(SimpleClientConfig.this), "/metrics");
      }
  
      // 如果你想要通过HTTPServer直接暴露metrics（通常不推荐在Spring Boot应用中使用）
      // 可以在某个初始化方法中启动HTTPServer
      // public void startMetricsServer() throws IOException {
      //     HTTPServer server = new HTTPServer(8081); // 监听8081端口
      //     // ...
      // }
  }
  ```

- 通过`MyMetricsServlet.java`配置使用`Servlet`暴露`simpleclient`创建的指标：

  ```java
  public class MyMetricsServlet extends MetricsServlet {
      private SimpleClientConfig simpleClientConfig;
      public static List<String> instanceList = new ArrayList<>(Arrays.asList("gce0", "gce1", "gce2"));
  
      public MyMetricsServlet(SimpleClientConfig simpleClientConfig) {
          super();
          this.simpleClientConfig = simpleClientConfig;
      }
  
      @Override
      protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
          Random random = new Random();
          String instance = instanceList.get(random.nextInt(instanceList.size()));
          // 操作Counter指标数据时指定instance标签的值为gce0、gce1、gce2
          this.simpleClientConfig.myCounter.labels(instance).inc();
  
          // 随机设置gauge的值，0-1的值表示0%-100%
          double randomDouble = Math.random();
          this.simpleClientConfig.myGauge.set(randomDouble);
  
          // 模拟最长的响应时间为60000毫秒
          int maxInt = 60000;
          int randomInt = random.nextInt(maxInt);
          // 模拟GET请求到/api/v1/data
          this.simpleClientConfig.requestDurationHistogram.labels("get", "/api/v1/data").observe(randomInt);
          // 模拟POST请求到/api/v1/submit
          this.simpleClientConfig.requestDurationHistogram.labels("post", "/api/v1/submit").observe(randomInt);
  
          randomInt = random.nextInt(maxInt);
          this.simpleClientConfig.summary.observe(randomInt);
  
          super.doGet(req, resp);
      }
  
      @Override
      protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
          super.doPost(req, resp);
      }
  }
  ```
  
- 打开浏览器访问`http://localhost:8080/metrics`（这个指标接口可以暴露给`prometheus`拉取数据）查看指标数据

配置 `Prometheus`：

- 在`prometheus.yml`配置文件中配置自定义`exporter`为拉取目标

  ```yaml
  scrape_configs:
    - job_name: 'prometheus'
      # 覆盖全局默认值，每15秒从该作业中刮取一次目标
      scrape_interval: 15s
      static_configs:
      # `prometheus`会自动添加`http://.../metrics`拉取`metrics`数据
      - targets: ['192.168.1.181:8081']
  ```

- 通过`prometheus`目标列表`http://localhost:9090/targets`查看新配置的`exporter`是否`UP`状态



## 使用 `Micrometer`

>提示：
>
>- `SpringBoot` 应用自定义指标的主流解决方案为 `Micrometer`。
>- 参考链接：https://medium.com/@ruth.kurniawati/publishing-prometheus-histograms-and-summaries-using-micrometer-in-a-spring-boot-application-d9ae6ba46660 使用 `DistributionSummary` 实现 `Histogram` 和 `Summary` 指标。详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/demo-prometheus/demo-spring-boot-actuator)。
>
>`todo`：为何 `DistributionSummary` 作为 `Histogram` 不能指定 `serviceLevelObjectives` 呢？只能指定 `maximumExpectedValue` 让其自动生成 `bucket`。

Micrometer 为最流行的可观察性系统提供了一个外观，让您可以对基于 JVM 的应用程序代码进行检测，而无需锁定供应商。类似于 SLF4J，但用于可观察性。它不是 prometheus 的客户端库，但是 Micrometer 默认被集成到 SpringBoot Actuator 中并且很方便地扩展自定义指标到 /actuator/metrics 端点中。

### 开发流程

Micrometer是一个应用程序监控库，支持多种监控系统，包括Prometheus。Spring Boot 2的actuator中默认使用Micrometer作为指标支持库，本身已经内置了许多开箱即用的指标。自定义的指标注册以后，也会被融合在相同的URI（/actuator/prometheus）中统一输出，非常方便。

**添加依赖：**

在Spring Boot项目的pom.xml文件中添加micrometer-registry-prometheus依赖，使Spring Boot Actuator能够以Prometheus可抓取的格式暴露指标。

```xml
<dependency>
	<groupId>io.micrometer</groupId>
	<artifactId>micrometer-registry-prometheus</artifactId>
</dependency>
```

**配置 `Prometheus` 端点：**

在application.properties或application.yml文件中启用Prometheus端点，并配置其他相关选项。

```properties
# 指定actuator端口
management.server.port=8081
management.endpoints.web.exposure.include=*
management.metrics.export.prometheus.enabled=true
```

**编写自定义指标：**

在Spring Boot应用中，可以通过定义MeterRegistry来创建指标并发送到Prometheus。例如，可以定义计数器（Counter）、仪表（Gauge）等类型的指标。

```java
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class PrometheusCustomMonitor {

	private Counter orderCount;
	private Gauge<Number> amountGauge;

	private final MeterRegistry registry;

	@Autowired
	public PrometheusCustomMonitor(MeterRegistry registry) {
		this.registry = registry;
	}

	@PostConstruct
	private void init() {
		orderCount = registry.counter("order_request_count", "order", "test-svc");
		amountGauge = Gauge.builder("order_amount_gauge", new AtomicInteger(0), AtomicInteger::get)
				.tags("orderAmount", "test-svc")
				.register(registry);
	}

	public void incrementOrderCount() {
		orderCount.increment();
	}

	public void setAmount(int amount) {
		((AtomicInteger) amountGauge.value()).set(amount);
	}
}
```

**在业务代码中使用自定义指标：**

在需要监控的业务代码中，调用自定义指标的方法来更新指标值。

```java
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

@RestController
public class OrderController {

	private final PrometheusCustomMonitor monitor;

	public OrderController(PrometheusCustomMonitor monitor) {
		this.monitor = monitor;
	}

	@GetMapping("/order")
	public String order() {
		// 统计下单次数
		monitor.incrementOrderCount();
		
		// 模拟订单金额
		Random random = new Random();
		int amount = random.nextInt(100);
		monitor.setAmount(amount);
		
		return "下单成功，金额: " + amount;
	}
}
```

**配置 `Prometheus` 抓取指标：**

在Prometheus的配置文件prometheus.yml中，添加对Spring Boot应用的抓取配置。

```yaml
global:
  scrape_interval: 15s

scrape_configs:
  - job_name: 'spring-boot'
	metrics_path: '/actuator/prometheus'
	static_configs:
	  - targets: ['localhost:8080'] # 替换为实际的Spring Boot应用地址
```

**在 `Grafana` 中展示指标：**

- 安装并运行 `Grafana`，然后添加 `Prometheus` 数据源。
- 创建新的仪表盘（Dashboard）和面板（Panel），在面板的查询部分输入自定义指标名称，例如 `order_request_count_total` 或 `order_amount_gauge`。
- 选择合适的图表类型（如 `Graph`、`Stat` 等）



### 实验

>详细用法请参考本站 [示例1](https://gitee.com/dexterleslie/demonstration/tree/main/demo-prometheus/demo-spring-boot-actuator) `+` [示例2](https://gitee.com/dexterleslie/demonstration/tree/main/demo-prometheus/demo-docker-compose-prometheus)

配置被监控的应用：

- `POM` 配置：

  ```xml
  <!-- 使用 micrometer 需要引入 actuator 以暴露指标到 /actuator/prometheus 接口 -->
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

- `application.properties` 配置启用 `Prometheus` 端点：

  ```properties
  # 指定actuator端口
  management.server.port=8081
  management.endpoints.web.exposure.include=*
  management.metrics.export.prometheus.enabled=true
  ```

- 创建自定义指标：

  `PrometheusCustomMonitor.java`：

  ```java
  @Component
  public class PrometheusCustomMonitor {
  
      private Counter orderCounterMaster;
      private Counter orderCounterDetail;
  
      private final MeterRegistry registry;
  
      @Resource
      AtomicInteger orderMasterTotalCount;
      @Resource
      AtomicInteger orderDetailTotalCount;
  
      @Autowired
      public PrometheusCustomMonitor(MeterRegistry registry) {
          this.registry = registry;
      }
  
      @PostConstruct
      private void init() {
          // 会自动转换名为为 my_order_request_count 的指标
          // 指标 my_order_request_count 的 tags 为 order='master'，模拟订单主表
          orderCounterMaster = registry.counter("my.order.request.count", "order", "master");
          // 指标 my_order_request_count 的 tags 为 order='detail'，模拟订单明细表
          orderCounterDetail = registry.counter("my.order.request.count", "order", "detail");
          registry.gauge("my.order.total", Collections.singletonList(new ImmutableTag("order", "master")), orderMasterTotalCount, AtomicInteger::get);
          registry.gauge("my.order.total", Collections.singletonList(new ImmutableTag("order", "detail")), orderDetailTotalCount, AtomicInteger::get);
      }
  
      public void incrementOrderCountMaster() {
          orderCounterMaster.increment();
      }
  
      public void incrementOrderCountDetail() {
          orderCounterDetail.increment(2);
      }
  }
  
  ```

  `ConfigMy.java`：

  ```java
  @Configuration
  public class ConfigMy {
      /**
       * 订单主表总数，gauge从此数据源获取，模拟从数据库读取订单总数
       *
       * @return
       */
      @Bean
      public AtomicInteger orderMasterTotalCount() {
          return new AtomicInteger();
      }
  
      /**
       * 订单明细表总数，gauge从此数据源获取，模拟从数据库读取订单总数
       *
       * @return
       */
      @Bean
      public AtomicInteger orderDetailTotalCount() {
          return new AtomicInteger();
      }
  }
  ```

  

- 在业务代码中使用自定义指标：

  ```java
  @RestController
  @RequestMapping("/api/v1")
  public class DemoController {
      @Resource
      PrometheusCustomMonitor monitor;
      @Resource
      AtomicInteger orderMasterTotalCount;
      @Resource
      AtomicInteger orderDetailTotalCount;
  
      @GetMapping("testCounter")
      public ObjectResponse<String> testCounter() {
          this.monitor.incrementOrderCountMaster();
          this.monitor.incrementOrderCountDetail();
          return ResponseUtils.successObject("成功下单");
      }
  
      /**
       * 测试设置订单主表总记录数
       *
       * @return
       */
      @GetMapping("testSetOrderMasterTotalCount")
      public ObjectResponse<String> testSetOrderMasterTotalCount() {
          this.orderMasterTotalCount.incrementAndGet();
          return ResponseUtils.successObject("调用成功");
      }
  
      /**
       * 测试设置订单明细表总记录数
       *
       * @return
       */
      @GetMapping("testSetOrderDetailTotalCount")
      public ObjectResponse<String> testSetOrderDetailTotalCount() {
          this.orderDetailTotalCount.incrementAndGet();
          return ResponseUtils.successObject("调用成功");
      }
  }
  ```

- 启动 `SpringBoot` 应用并检查 `Prometheus` 端点是否已经启用，访问 `http://localhost:8081/actuator/prometheus` 并搜索是否存在自定义指标 `my_order_request_count` 和 `my_order_total`。

配置 `Prometheus` 抓取指标：

- `prometheus.yml` 配置如下：

  ```yaml
  # 搜刮配置
  scrape_configs:
    - job_name: 'spring-boot actuator'
      scrape_interval: 15s
      metrics_path: '/actuator/prometheus'
      static_configs:
      - targets: ['192.168.1.181:8081']
        labels:
          instance: 'spring-boot actuator'
  ```

- 检查 `Prometheus` 是否成功抓取 `SpringBoot` 应用中的自定义指标：

  打开 `Prometheus promql` 面板 `http://localhost:9090/graph`，输入指标 `my_order_request_count_total`，如果有数据显示表示 `Prometheus` 成功抓取 `SpringBoot` 应用中的自定义指标。

在 `Grafana` 中显示自定义指标：参考本站 <a href="/prometheus/grafana.html#在-grafana-中配置显示自定义指标" target="_blank">链接</a>

使用 `wrk` 产生测试数据：

```sh
wrk -t1 -c1 -d150000s --latency --timeout 30 http://192.168.1.181:8080/api/v1/testCounter

wrk -t1 -c1 -d150000s --latency --timeout 30 http://192.168.1.181:8080/api/v1/testSetOrderMasterTotalCount

wrk -t1 -c1 -d150000s --latency --timeout 30 http://192.168.1.181:8080/api/v1/testSetOrderDetailTotalCount

wrk -t1 -c1 -d150000s --latency --timeout 30 http://192.168.1.181:8080/api/v1/testAssistRequestLatency
```



### 动态标签

>详细用法请参考本站 [示例](https://gitee.com/dexterleslie/demonstration/tree/main/demo-prometheus/demo-spring-boot-actuator)

#### 基本用法

运行示例后，使用 `api.http` 中的 `GET http://localhost:8080/api/v1/testAssistRequestLatency` 生成测试数据。

访问 `http://localhost:8081/actuator/prometheus` 并搜索 `http_request_duration` 查看数据

```
# HELP http_request_duration_seconds_max Request latency in seconds.
# TYPE http_request_duration_seconds_max gauge
http_request_duration_seconds_max{method="put",url="/api/v1/test2",} 33.513
http_request_duration_seconds_max{method="put",url="/api/v1/test1",} 43.821
http_request_duration_seconds_max{method="get",url="/api/v1/test1",} 3.084
http_request_duration_seconds_max{method="get",url="/api/v1/test2",} 57.14
# HELP http_request_duration_seconds Request latency in seconds.
# TYPE http_request_duration_seconds histogram
http_request_duration_seconds_bucket{method="put",url="/api/v1/test2",le="0.01",} 0.0
http_request_duration_seconds_bucket{method="put",url="/api/v1/test2",le="0.1",} 0.0
http_request_duration_seconds_bucket{method="put",url="/api/v1/test2",le="0.2",} 0.0
http_request_duration_seconds_bucket{method="put",url="/api/v1/test2",le="0.3",} 0.0
http_request_duration_seconds_bucket{method="put",url="/api/v1/test2",le="0.5",} 0.0
http_request_duration_seconds_bucket{method="put",url="/api/v1/test2",le="1.0",} 0.0
http_request_duration_seconds_bucket{method="put",url="/api/v1/test2",le="5.0",} 0.0
http_request_duration_seconds_bucket{method="put",url="/api/v1/test2",le="10.0",} 0.0
http_request_duration_seconds_bucket{method="put",url="/api/v1/test2",le="30.0",} 1.0
http_request_duration_seconds_bucket{method="put",url="/api/v1/test2",le="+Inf",} 2.0
http_request_duration_seconds_count{method="put",url="/api/v1/test2",} 2.0
http_request_duration_seconds_sum{method="put",url="/api/v1/test2",} 55.282
http_request_duration_seconds_bucket{method="put",url="/api/v1/test1",le="0.01",} 0.0
http_request_duration_seconds_bucket{method="put",url="/api/v1/test1",le="0.1",} 0.0
http_request_duration_seconds_bucket{method="put",url="/api/v1/test1",le="0.2",} 0.0
http_request_duration_seconds_bucket{method="put",url="/api/v1/test1",le="0.3",} 0.0
http_request_duration_seconds_bucket{method="put",url="/api/v1/test1",le="0.5",} 0.0
http_request_duration_seconds_bucket{method="put",url="/api/v1/test1",le="1.0",} 0.0
http_request_duration_seconds_bucket{method="put",url="/api/v1/test1",le="5.0",} 0.0
http_request_duration_seconds_bucket{method="put",url="/api/v1/test1",le="10.0",} 1.0
http_request_duration_seconds_bucket{method="put",url="/api/v1/test1",le="30.0",} 2.0
http_request_duration_seconds_bucket{method="put",url="/api/v1/test1",le="+Inf",} 3.0
http_request_duration_seconds_count{method="put",url="/api/v1/test1",} 3.0
http_request_duration_seconds_sum{method="put",url="/api/v1/test1",} 66.331
http_request_duration_seconds_bucket{method="get",url="/api/v1/test1",le="0.01",} 0.0
http_request_duration_seconds_bucket{method="get",url="/api/v1/test1",le="0.1",} 0.0
http_request_duration_seconds_bucket{method="get",url="/api/v1/test1",le="0.2",} 0.0
http_request_duration_seconds_bucket{method="get",url="/api/v1/test1",le="0.3",} 0.0
http_request_duration_seconds_bucket{method="get",url="/api/v1/test1",le="0.5",} 0.0
http_request_duration_seconds_bucket{method="get",url="/api/v1/test1",le="1.0",} 0.0
http_request_duration_seconds_bucket{method="get",url="/api/v1/test1",le="5.0",} 1.0
http_request_duration_seconds_bucket{method="get",url="/api/v1/test1",le="10.0",} 1.0
http_request_duration_seconds_bucket{method="get",url="/api/v1/test1",le="30.0",} 1.0
http_request_duration_seconds_bucket{method="get",url="/api/v1/test1",le="+Inf",} 1.0
http_request_duration_seconds_count{method="get",url="/api/v1/test1",} 1.0
http_request_duration_seconds_sum{method="get",url="/api/v1/test1",} 3.084
http_request_duration_seconds_bucket{method="get",url="/api/v1/test2",le="0.01",} 0.0
http_request_duration_seconds_bucket{method="get",url="/api/v1/test2",le="0.1",} 0.0
http_request_duration_seconds_bucket{method="get",url="/api/v1/test2",le="0.2",} 0.0
http_request_duration_seconds_bucket{method="get",url="/api/v1/test2",le="0.3",} 0.0
http_request_duration_seconds_bucket{method="get",url="/api/v1/test2",le="0.5",} 0.0
http_request_duration_seconds_bucket{method="get",url="/api/v1/test2",le="1.0",} 0.0
http_request_duration_seconds_bucket{method="get",url="/api/v1/test2",le="5.0",} 0.0
http_request_duration_seconds_bucket{method="get",url="/api/v1/test2",le="10.0",} 0.0
http_request_duration_seconds_bucket{method="get",url="/api/v1/test2",le="30.0",} 0.0
http_request_duration_seconds_bucket{method="get",url="/api/v1/test2",le="+Inf",} 2.0
http_request_duration_seconds_count{method="get",url="/api/v1/test2",} 2.0
http_request_duration_seconds_sum{method="get",url="/api/v1/test2",} 91.431
```

动态标签代码如下：

```java
List<String> methodList = Arrays.asList("get", "put");
List<String> urlList = Arrays.asList("/api/v1/test1", "/api/v1/test2");

/**
 * 协助测试请求延迟直方图
 *
 * @return
 */
@GetMapping("testAssistRequestLatency")
public ObjectResponse<String> testAssistRequestLatency() {
    String method = methodList.get(RandomUtil.randomInt(0, methodList.size()));
    String url = urlList.get(RandomUtil.randomInt(0, urlList.size()));
    int milliseconds = RandomUtil.randomInt(0, 60000);
    // 模拟请求延迟直方图
    Timer.builder("http_request_duration")
            .description("Request latency in seconds.")
            // 桶中的区间的分布如下 10-100（单位是毫秒）、100-200、...
            .serviceLevelObjectives(
                    Duration.ofMillis(10),
                    Duration.ofMillis(100),
                    Duration.ofMillis(200),
                    Duration.ofMillis(300),
                    Duration.ofMillis(500),
                    Duration.ofMillis(1000),
                    Duration.ofMillis(5000),
                    Duration.ofMillis(10000),
                    Duration.ofMillis(30000)
            )
            .tags("method", method, "url", url)
            .register(meterRegistry).record(Duration.ofMillis(milliseconds));
    return ResponseUtils.successObject("调用成功");
}
```



#### 可以重复调用 `builder` 获取指标实例

下面代码用于声明动态标签场景使用 `builder` 和相同的参数能够获取到之前已经创建的同一个实例（可以间接推导重复调用 `builder` 不会导致内存泄漏）：

```java
/**
 * 测试动态标签场景使用 builder 和相同的参数是否会返回相同的实例
 */
@Test
public void testDynamicTagsCheckIfBuilderReturnTheSameInstance() {
    // region 测试 Timer

    Timer timer1 = Timer.builder("http_request_duration")
            .description("Request latency in seconds.")
            // 桶中的区间的分布如下 10-100（单位是毫秒）、100-200、...
            .serviceLevelObjectives(
                    Duration.ofMillis(10),
                    Duration.ofMillis(100),
                    Duration.ofMillis(200),
                    Duration.ofMillis(300),
                    Duration.ofMillis(500),
                    Duration.ofMillis(1000),
                    Duration.ofMillis(5000),
                    Duration.ofMillis(10000),
                    Duration.ofMillis(30000)
            )
            .tags("k1", "v1", "k2", "v2")
            .register(registry);

    Timer timer2 = Timer.builder("http_request_duration")
            .description("Request latency in seconds.")
            // 桶中的区间的分布如下 10-100（单位是毫秒）、100-200、...
            .serviceLevelObjectives(
                    Duration.ofMillis(10),
                    Duration.ofMillis(100),
                    Duration.ofMillis(200),
                    Duration.ofMillis(300),
                    Duration.ofMillis(500),
                    Duration.ofMillis(1000),
                    Duration.ofMillis(5000),
                    Duration.ofMillis(10000),
                    Duration.ofMillis(30000)
            )
            .tags("k1", "v1", "k2", "v2")
            .register(registry);
    Assertions.assertEquals(timer1, timer2);

    // 测试 tags 不一样时不是同一个实例
    Timer timer3 = Timer.builder("http_request_duration")
            .description("Request latency in seconds.")
            // 桶中的区间的分布如下 10-100（单位是毫秒）、100-200、...
            .serviceLevelObjectives(
                    Duration.ofMillis(10),
                    Duration.ofMillis(100),
                    Duration.ofMillis(200),
                    Duration.ofMillis(300),
                    Duration.ofMillis(500),
                    Duration.ofMillis(1000),
                    Duration.ofMillis(5000),
                    Duration.ofMillis(10000),
                    Duration.ofMillis(30000)
            )
            .tags("k1", "v1", "k2", "v21")
            .register(registry);
    Assertions.assertNotEquals(timer1, timer3);

    // 测试桶分布不一样时也是同一个实例
    Timer timer4 = Timer.builder("http_request_duration")
            .description("Request latency in seconds.")
            // 桶中的区间的分布如下 10-100（单位是毫秒）、100-200、...
            .serviceLevelObjectives(
                    Duration.ofMillis(10),
                    Duration.ofMillis(100),
                    Duration.ofMillis(200),
                    Duration.ofMillis(300),
                    Duration.ofMillis(500),
                    Duration.ofMillis(1000),
                    /*Duration.ofMillis(5000),*/
                    Duration.ofMillis(10000),
                    Duration.ofMillis(30000)
            )
            .tags("k1", "v1", "k2", "v2")
            .register(registry);
    Assertions.assertEquals(timer1, timer4);

    // endregion

    // region 测试 Counter

    Counter counter1 = Counter.builder("counter")
            .description("desc1")
            .tags("k1", "v1", "k2", "v2")
            .register(registry);
    Counter counter2 = Counter.builder("counter")
            .description("desc1")
            .tags("k1", "v1", "k2", "v2")
            .register(registry);
    Assertions.assertEquals(counter1, counter2);

    // 测试描述不一样时也是同一个实例
    Counter counter3 = Counter.builder("counter")
            .description("desc11")
            .tags("k1", "v1", "k2", "v2")
            .register(registry);
    Assertions.assertEquals(counter1, counter3);

    // 测试名称不一样时不是同一个实例
    Counter counter4 = Counter.builder("counter1")
            .description("desc1")
            .tags("k1", "v1", "k2", "v2")
            .register(registry);
    Assertions.assertNotEquals(counter1, counter4);

    // 测试 tags 不一样时不是同一个实例
    Counter counter5 = Counter.builder("counter")
            .description("desc1")
            .tags("k1", "v1", "k2", "v21")
            .register(registry);
    Assertions.assertNotEquals(counter1, counter5);

    // endregion

    // region 测试 Gauge

    Gauge gauge1 = Gauge.builder("gauge", new Supplier<Number>() {
        @Override
        public Number get() {
            return null;
        }
    }).tags("k1", "v1", "k2", "v2").register(registry);
    Gauge gauge2 = Gauge.builder("gauge", new Supplier<Number>() {
        @Override
        public Number get() {
            return null;
        }
    }).tags("k1", "v1", "k2", "v2").register(registry);
    Assertions.assertEquals(gauge1, gauge2);

    // 测试 tags 不一样时不是同一个实例
    Gauge gauge3 = Gauge.builder("gauge", new Supplier<Number>() {
        @Override
        public Number get() {
            return null;
        }
    }).tags("k1", "v1", "k2", "v21").register(registry);
    Assertions.assertNotEquals(gauge1, gauge3);

    // endregion
}
```



#### 测试重复调用 `builder` 获取指标实例是否会内存泄漏

设置应用 `-Xmx256m` 并启动，使用 `wrk` 协助测试：

```sh
wrk -t4 -c16 -d150000s --latency --timeout 60 http://localhost:8080/api/v1/testAssistRequestLatency
```

持续运行约 `1` 个小时应用没有报告内存溢出错误。

或者参考本站 <a href="/java/arthas使用.html#memory使用" target="_blank">链接</a> 使用 `arthas` 的 `memory` 命令观察老年代内存没有持续增长表示没有内存溢出错误。



## `simpleclient`和`Micrometer`比较

Prometheus的simpleclient和Micrometer都是用于监控和度量数据的工具，但它们在使用场景、集成方式和功能特性上有所不同。以下是对两者的详细比较：

**一、Prometheus Simpleclient**

1. 定义与用途：
   - Prometheus Simpleclient是Prometheus官方提供的Java客户端库，用于收集和暴露应用程序的监控指标给Prometheus服务器。
2. 工作原理：
   - Simpleclient内部封装了基本的数据结构和数据采集方式，提供自定义监控指标的核心接口。
   - 通过simpleclient_httpserver模块，可以实现一个简单的HTTP服务器，当向该服务器发送获取样本数据的请求后，它会自动调用所有Collector的collect()方法，并将所有样本数据转换为Prometheus要求的数据输出格式规范。
3. 使用方式：
   - 通常需要在项目中添加simpleclient相关的依赖，并编写自定义的Collector类来实现监控指标的收集。
   - 然后，通过启动HTTP服务器来暴露监控指标，Prometheus服务器会主动拉取这些指标。

**二、Micrometer**

1. 定义与用途：
   - Micrometer是一个应用度量库，用于监控和报告JVM应用的各种度量指标，如CPU使用率、内存占用、HTTP请求响应时间等。
   - 它支持多种监控后端，包括Prometheus、Atlas、Graphite、InfluxDB等。
2. 工作原理：
   - Micrometer通过MeterRegistry来创建和管理各种Meter（度量接口），如Counter、Gauge、Timer等。
   - MeterRegistry是Meter的工厂和缓存中心，每个JVM应用在使用Micrometer时必须创建一个MeterRegistry的具体实现。
   - Micrometer支持自定义命名规则和标签（Tag），以便更好地组织和查询度量数据。
3. 使用方式：
   - 对于Spring Boot项目，可以通过添加spring-boot-starter-actuator和micrometer-core依赖来集成Micrometer。
   - 在配置文件中指定监控后端（如Prometheus），并配置相应的度量指标。
   - 通过Actuator提供的/metrics端点，可以访问和查询度量数据。

**三、比较与选择**

1. 集成方式：
   - Simpleclient需要手动编写代码来收集和暴露监控指标，适用于对监控需求有较高自定义要求的场景。
   - Micrometer则提供了更为丰富和灵活的度量类型和配置选项，且易于与Spring Boot等框架集成，适用于快速构建和部署监控系统的场景。
2. 功能特性：
   - Simpleclient专注于提供基本的监控指标收集和暴露功能，适合对监控需求较为简单的场景。
   - Micrometer则提供了更多的度量类型和高级特性，如标签、命名规则转换等，适用于对监控需求较为复杂的场景。
3. 生态系统：
   - Prometheus Simpleclient是Prometheus生态系统的一部分，与Prometheus服务器和Grafana等数据可视化工具紧密集成。
   - Micrometer则是一个独立的度量库，可以与其他监控后端和可视化工具配合使用，提供了更广泛的生态系统支持。

综上所述，Prometheus Simpleclient和Micrometer各有优缺点，选择哪个取决于具体的监控需求和项目背景。对于需要高度自定义监控指标和与Prometheus紧密集成的场景，可以选择Simpleclient；而对于需要快速构建和部署监控系统、且对度量类型和配置选项有较高要求的场景，则可以选择Micrometer。