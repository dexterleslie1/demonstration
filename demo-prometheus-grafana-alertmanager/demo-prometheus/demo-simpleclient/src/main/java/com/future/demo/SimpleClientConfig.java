package com.future.demo;

import io.prometheus.client.Counter;
import io.prometheus.client.Gauge;
import io.prometheus.client.Histogram;
import io.prometheus.client.Summary;
import lombok.Getter;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Servlet;

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
