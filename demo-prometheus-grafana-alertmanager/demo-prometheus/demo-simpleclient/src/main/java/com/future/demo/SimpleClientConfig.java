package com.future.demo;

import io.prometheus.client.Counter;
import io.prometheus.client.Gauge;
import io.prometheus.client.exporter.MetricsServlet;
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
            .labelNames("label_type")
            .register();

    // 创建一个Gauge
    Gauge myGauge = Gauge.build()
            .name("my_custom_gauge")
            .help("这是自定义gauge指标类型")
            .labelNames("label_type")
            .register();

    // 在某处（如Controller或服务中）增加Counter的值
    // ...

    // 注册MetricsServlet以暴露metrics
    @Bean
    public ServletRegistrationBean<Servlet> servletRegistrationBean() {
        return new ServletRegistrationBean<>(new MyMetricsServlet(myCounter, myGauge), "/metrics");
    }

    // 如果你想要通过HTTPServer直接暴露metrics（通常不推荐在Spring Boot应用中使用）
    // 可以在某个初始化方法中启动HTTPServer
    // public void startMetricsServer() throws IOException {
    //     HTTPServer server = new HTTPServer(8081); // 监听8081端口
    //     // ...
    // }
}
