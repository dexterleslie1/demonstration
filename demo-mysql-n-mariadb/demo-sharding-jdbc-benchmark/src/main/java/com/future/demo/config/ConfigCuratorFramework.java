package com.future.demo.config;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConfigCuratorFramework {
    @Value("${zookeeper.url}")
    String url;

    @Bean(destroyMethod = "close")
    CuratorFramework curatorFramework() {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        CuratorFramework curatorFramework =
                CuratorFrameworkFactory.newClient(
                        url,
                        5000,
                        3000,
                        retryPolicy);
        curatorFramework.start();
        return curatorFramework;
    }
}