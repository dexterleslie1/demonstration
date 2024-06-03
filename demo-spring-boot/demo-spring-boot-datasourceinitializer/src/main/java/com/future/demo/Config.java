package com.future.demo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;

import javax.sql.DataSource;

@Configuration
public class Config {
    // 注入DataSourceInitializer到spring容器中才会执行DataSourceInitializer中的逻辑
    // 注意：spring容器启动后每次都会执行DataSourceInitializer中配置的脚本，所以在编写脚本时需要保证脚本是幂等的
    @Bean
    public DataSourceInitializer dataSourceInitializer1(DataSource dataSource) {
        return new MyDataSourceInitializer1(dataSource);
    }

    @Bean
    public DataSourceInitializer dataSourceInitializer2(DataSource dataSource) {
        return new MyDataSourceInitializer2(dataSource);
    }
}
