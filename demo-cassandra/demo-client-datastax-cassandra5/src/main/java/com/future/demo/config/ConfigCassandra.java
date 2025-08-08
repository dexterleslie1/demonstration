package com.future.demo.config;

import com.datastax.oss.driver.api.core.CqlSession;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetSocketAddress;

@Configuration
public class ConfigCassandra {
    // 以下是连接 cassandra5 驱动程序使用
    @Bean(destroyMethod = "close")
    public CqlSession cqlSession() {
        return CqlSession.builder()
                .addContactPoint(new InetSocketAddress("localhost", 9042))
                .addContactPoint(new InetSocketAddress("localhost", 9043))
                // 指定本地数据中心名称
                .withLocalDatacenter("datacenter1")
                .withKeyspace("demo")
                .build();
    }
}
