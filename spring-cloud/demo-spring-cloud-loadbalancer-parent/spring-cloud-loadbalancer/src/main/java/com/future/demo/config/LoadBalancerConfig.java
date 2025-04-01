package com.future.demo.config;

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClient;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClients;
import org.springframework.cloud.loadbalancer.core.ReactorLoadBalancer;
import org.springframework.cloud.loadbalancer.core.RoundRobinLoadBalancer;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.cloud.loadbalancer.support.LoadBalancerClientFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.client.RestTemplate;

@Configuration
@LoadBalancerClients(
        // spring-cloud-helloworld 使用 LoadBalancerConfig 配置的负载均衡算法 RoundRobinLoadBalancer
        @LoadBalancerClient(value = "spring-cloud-helloworld", configuration = LoadBalancerConfig.class))
public class LoadBalancerConfig {
    // 创建RestTemplate并开启负载均衡
    @Bean
    @LoadBalanced
    RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    ReactorLoadBalancer<ServiceInstance> loadBalancer(Environment environment,
                                                      LoadBalancerClientFactory loadBalancerClientFactory) {
        String name = environment.getProperty(LoadBalancerClientFactory.PROPERTY_NAME);
        // 随机负载均衡算法
        /*return new RandomLoadBalancer(loadBalancerClientFactory
                .getLazyProvider(name, ServiceInstanceListSupplier.class),
                name);*/
        // 轮询负载均衡算法
        return new RoundRobinLoadBalancer(loadBalancerClientFactory
                .getLazyProvider(name, ServiceInstanceListSupplier.class),
                name);
    }
}
