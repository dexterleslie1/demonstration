package com.future.demo.config;

import com.datastax.oss.driver.api.core.CqlSession;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetSocketAddress;

@Configuration
public class ConfigCassandra {
    @Bean(destroyMethod = "close")
    public CqlSession cqlSession() {
        String host = "localhost";
        int port = 9042;
        return CqlSession.builder()
                .addContactPoint(new InetSocketAddress(host, port))
                // 指定本地数据中心名称
                .withLocalDatacenter("datacenter1")
                .withKeyspace("demo")
                .build();
    }
}
