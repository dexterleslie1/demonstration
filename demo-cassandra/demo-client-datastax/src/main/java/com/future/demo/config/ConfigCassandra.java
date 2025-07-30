package com.future.demo.config;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConfigCassandra {
    // 以下是连接 cassandra5 驱动程序使用
    /*@Bean(destroyMethod = "close")
    public CqlSession cqlSession() {
        String host = "localhost";
        int port = 9042;
        return CqlSession.builder()
                .addContactPoint(new InetSocketAddress(host, port))
                // 指定本地数据中心名称
                .withLocalDatacenter("datacenter1")
                .withKeyspace("demo")
                .build();
    }*/

    @Bean(destroyMethod = "close")
    public Cluster cluster() {
        return Cluster.builder()
                .addContactPoint("localhost").withPort(9042)
                .addContactPoint("localhost").withPort(9043)
//                .addContactPoint("localhost").withPort(9044)
                .build();
    }

    // session 是线程安全的，共用同一个 session
    @Bean(destroyMethod = "close")
    public Session session(Cluster cluster) {
        return cluster.connect("demo");
    }
}
