# 客户端库

## `java`客户端库

### `simpleclient`

> 注意：`prometheus`被设计为拉取指标模式，`simpleclient`不存在主动连接`prometheus`服务器推送指标数据的概念。
>
> `simpleclient`详细使用方法请参考`https://gitee.com/dexterleslie/demonstration/tree/master/demo-prometheus/demo-prometheus-simpleclient`

Prometheus `simpleclient` 是 Prometheus 官方提供的 Java 客户端库，它允许 Java 应用程序创建和暴露 metrics，这些 metrics 可以被 Prometheus 服务器拉取以进行监控和告警。`simpleclient` 提供了一组简单的 API，用于定义和注册计数器（Counter）、直方图（Histogram）、摘要（Summary）和仪表（Gauge）等类型的 metrics。

使用 `simpleclient`，Java 应用程序可以轻松地暴露其内部状态和运行指标，例如请求数、处理时间、内存使用情况等。这些指标可以通过 HTTP 接口（通常是 `/metrics` 端点）进行访问，Prometheus 服务器会定期拉取这些指标数据，并存储在本地以供查询和可视化。

`simpleclient` 库还包含一些额外的模块，用于与其他 Java 组件集成和简化常见任务的执行。例如，`simpleclient_hotspot` 模块可以自动收集和暴露 JVM 的热点指标（如类加载、垃圾回收等），而 `simpleclient_servlet` 模块可以与 Java Servlet 容器（如 Tomcat、Jetty）集成，以在 Servlet 应用程序中轻松暴露 metrics。

虽然 `simpleclient` 提供了一个基础的 Prometheus 客户端实现，但在实际应用中，特别是在使用 Spring Boot 或其他现代 Java 框架时，更常见的做法是使用 Micrometer 这样的库。Micrometer 是一个通用的度量库，它提供了对多种监控系统的支持，包括 Prometheus，并提供了与 Spring Boot 等框架的集成，使得集成和配置变得更加简单和直观。

总之，Prometheus `simpleclient` 是 Prometheus 官方提供的 Java 客户端库，用于在 Java 应用程序中创建和暴露 metrics，以便 Prometheus 服务器进行监控和告警。然而，在实际应用中，使用 Micrometer 这样的库可能更为常见和方便。

`maven pom.xml`引用`simpleclient`

```xml
<!-- simpleclient_servlet会自动引入simpleclient -->
<dependency>
    <groupId>io.prometheus</groupId>
    <artifactId>simpleclient_servlet</artifactId>
    <version>0.16.0</version>
</dependency>
```

`springboot`项目`SimpleClientConfig.java`配置如下，例子中创建`Counter`、`Gauge`、`Histogram`、`Summary`指标分别演示其各自的用法

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

通过`MyMetricsServlet.java`配置使用`Servlet`暴露`simpleclient`创建的指标

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

打开浏览器访问`http://localhost:8080/metrics`（这个指标接口可以暴露给`prometheus`拉取数据）查看指标数据



### `micrometer`

Micrometer 为最流行的可观察性系统提供了一个外观，让您可以对基于 JVM 的应用程序代码进行检测，而无需锁定供应商。类似于 SLF4J，但用于可观察性。

micrometer 不是 prometheus 的客户端库，但是 micrometer 默认被集成到 SpringBoot Actuator 中并且很方便地扩展自定义指标到 /actuator/metrics 端点中。



### `simpleclient`和`micrometer`比较

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