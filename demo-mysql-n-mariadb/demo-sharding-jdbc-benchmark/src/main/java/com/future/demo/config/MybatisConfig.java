package com.future.demo.config;

import com.github.pagehelper.PageInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

// 配置mapper扫描包路径
@MapperScan("com.future.demo.mapper")
@Configuration
public class MybatisConfig {
    /**
     * pagehelper分页插件配置
     *
     * @return
     */
    @Bean
    public PageInterceptor pageInterceptor() {
        PageInterceptor interceptor = new PageInterceptor();
        Properties properties = new Properties();
        // 启用合理化，如果pageNum<1会查询第一页，如果pageNum>pages会查询最后一页
        properties.setProperty("reasonable", "true");
        interceptor.setProperties(properties);
        return interceptor;
    }
}
