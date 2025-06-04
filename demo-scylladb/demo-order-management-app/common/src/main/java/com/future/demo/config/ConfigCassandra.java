package com.future.demo.config;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConfigCassandra {

    @Bean(destroyMethod = "close")
    public Cluster cluster() {
        return Cluster.builder()
                /*.addContactPoint("localhost").withPort(9042)
                .addContactPoint("localhost").withPort(9043)
                .addContactPoint("localhost").withPort(9045)*/
                /*.addContactPoint("192.168.1.90").withPort(9042)
                .addContactPoint("192.168.1.91").withPort(9042)
                .addContactPoint("192.168.1.92").withPort(9042)*/
                .addContactPoint("172.16.110.78").withPort(9042)
                .addContactPoint("172.16.110.79").withPort(9042)
                .addContactPoint("172.16.110.80").withPort(9042)
                .build();
    }

    @Bean(destroyMethod = "close")
    public Session session(Cluster cluster) {
        return cluster.connect("demo");
    }
}
