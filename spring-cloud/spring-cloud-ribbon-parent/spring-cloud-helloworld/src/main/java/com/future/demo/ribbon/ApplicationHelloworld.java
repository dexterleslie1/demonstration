package com.future.demo.ribbon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author dexterleslie@gmail.com
 */
@SpringBootApplication
@EnableDiscoveryClient
public class ApplicationHelloworld {
    /**
     * @param args
     */
    public static void main(String[] args) {
        SpringApplication.run(ApplicationHelloworld.class, args);
    }
}