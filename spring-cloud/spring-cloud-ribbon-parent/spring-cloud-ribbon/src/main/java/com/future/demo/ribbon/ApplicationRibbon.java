package com.future.demo.ribbon;

import com.future.demo.myrule.MyRuleRandom;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

/**
 * @author dexterleslie@gmail.com
 */
@SpringBootApplication
@RibbonClient(name = "spring-cloud-helloworld", configuration = MyRuleRandom.class)
@EnableDiscoveryClient
public class ApplicationRibbon {
    /**
     *
     * @param args
     */
    public static void main(String[] args) {
        SpringApplication.run(ApplicationRibbon.class, args);
    }

    /**
     * 无论是否何种Ribbon负载均衡算法都需要配置下面的RestTemplate
     * @return
     */
    @Bean
    @LoadBalanced
    RestTemplate restTemplate() {
        return new RestTemplate();
    }
}