package com.future.demo.ribbon;

import com.future.demo.myrule.MyRuleRandom;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * @author dexterleslie@gmail.com
 */
@SpringBootApplication
@RibbonClient(name = "spring-cloud-helloworld", configuration = MyRuleRandom.class)
@EnableDiscoveryClient
public class ApplicationRibbon {
    /**
     * @param args
     */
    public static void main(String[] args) {
        SpringApplication.run(ApplicationRibbon.class, args);
    }

    /**
     * 无论是否何种Ribbon负载均衡算法都需要配置下面的RestTemplate
     *
     * @return
     */
    @Bean
    @LoadBalanced
    RestTemplate restTemplate() {
        // RestTemplate 默认使用 Java 标准库的 HttpURLConnection 作为底层 HTTP 通信实现
        // 通过下面的配置把 HTTP 通讯实现切换为 Apache HttpClient

        // 创建HttpClient连接池配置
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        // 设置最大连接数
        connectionManager.setMaxTotal(65535);
        // 设置每个路由默认最大连接数
        connectionManager.setDefaultMaxPerRoute(65535);
        // 创建HttpClient
        CloseableHttpClient httpClient = HttpClientBuilder.create()
                .setConnectionManager(connectionManager)
                .build();
        // 使用HttpClient创建请求工厂
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setHttpClient(httpClient);
        // 连接超时5秒
        factory.setConnectTimeout(5000);
        // 读取超时10秒
        factory.setReadTimeout(10000);
        // 从连接池获取连接的超时时间
        factory.setConnectionRequestTimeout(10000);

        return new RestTemplate(factory);

        // 下面创建 RestTemplate 方式默认使用 Java 标准库的 HttpURLConnection 作为底层 HTTP 通信实现
        // 超时默认配置为无限制
        // return new RestTemplate();
    }
}