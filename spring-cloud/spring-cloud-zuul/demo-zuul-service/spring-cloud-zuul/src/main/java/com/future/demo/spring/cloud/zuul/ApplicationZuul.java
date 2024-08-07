package com.future.demo.spring.cloud.zuul;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;

/**
 * @author dexterleslie@gmail.com
 */
@SpringBootApplication
@EnableZuulProxy
public class ApplicationZuul {
    /**
     * @param args
     */
    public static void main(String[] args) {
        SpringApplication.run(ApplicationZuul.class, args);
    }

    @Bean
    UploadAndDownloadFilter uploadAndDownloadFilter() {
        return new UploadAndDownloadFilter();
    }
}