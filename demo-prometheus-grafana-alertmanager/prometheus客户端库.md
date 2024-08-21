# 客户端库

## `java`客户端库

### `simpleclient`

> 注意：`prometheus`被设计为拉取指标模式，`simpleclient`不存在主动连接`prometheus`服务器推送指标数据的概念。
>
> `simpleclient`详细使用方法请参考 [链接](https://gitee.com/dexterleslie/demonstration/tree/master/demo-prometheus-grafana-alertmanager/demo-prometheus/demo-simpleclient)

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