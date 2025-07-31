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
                // 如果只有一个 localhost:9042 连接点，
                // 在执行 nodetool decommission 使节点删除后将无法获取到集群新的拓扑导致报错
                .addContactPoint("localhost").withPort(9042)
                .addContactPoint("localhost").withPort(9043)
                .build();
    }

    // session 是线程安全的，共用同一个 session
    @Bean(destroyMethod = "close")
    public Session session(Cluster cluster) {
        return cluster.connect("demo");
    }
}
