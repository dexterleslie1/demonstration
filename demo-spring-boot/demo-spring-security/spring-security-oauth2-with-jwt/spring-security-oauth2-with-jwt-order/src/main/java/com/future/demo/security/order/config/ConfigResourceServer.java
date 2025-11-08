package com.future.demo.security.order.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;

@Configuration
// order-service作为资源服务器
@EnableResourceServer
public class ConfigResourceServer extends ResourceServerConfigurerAdapter {
    @Override
    public void configure(ResourceServerSecurityConfigurer resources) {
        resources
                // 资源ID，和客户端配置的资源ID一致
                .resourceId("order-service")
                // 服务器不会创建或维护会话（HttpSession）
                .stateless(true);
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
                // 禁用csrf
                .csrf().disable()
                // 服务器不会创建或维护会话（HttpSession）
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                // OAuth2客户端要有write权限范围
                .and().authorizeRequests()
                .antMatchers("/**").access("#oauth2.hasScope('write')");
    }
}
