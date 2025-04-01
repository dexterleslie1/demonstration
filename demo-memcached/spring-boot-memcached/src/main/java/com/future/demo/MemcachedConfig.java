package com.future.demo;

import net.spy.memcached.MemcachedClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.net.InetSocketAddress;

@Configuration
public class MemcachedConfig {

    @Value("${memcache.ip}")
    private String ip;

    @Value("${memcache.port}")
    private int port;


    @Bean
    public MemcachedClient getClient() {
        MemcachedClient memcachedClient = null;
        try {
            memcachedClient  = new MemcachedClient(new InetSocketAddress(ip, port));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return memcachedClient;
    }
}
