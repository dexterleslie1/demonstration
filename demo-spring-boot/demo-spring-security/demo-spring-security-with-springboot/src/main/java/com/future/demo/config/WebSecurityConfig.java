package com.future.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    // 定义用户信息服务
    @Bean
    protected UserDetailsService userDetailsService() {
        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
        manager.createUser(User.withUsername("abc1").password("123456").authorities("p1").build());
        manager.createUser(User.withUsername("abc2").password("123456").authorities("p2").build());
        return manager;
    }

    // 密码编码器
    @Bean
    PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

    // 安全拦截配置
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // 定义哪些URL路径需要被保护，以及这些路径应该应用哪些安全规则。通过这个方法，你可以指定哪些角色或权限的用户可以访问特定的资源。
        http.authorizeRequests()
                // /r/r1 路径下的资源需要拥有 p1 权限的用户才能访问
                .antMatchers("/r/r1").hasAuthority("p1")
                // /r/r2 路径下的资源需要拥有 p2 权限的用户才能访问
                .antMatchers("/r/r2").hasAuthority("p2")
                // /r/** 路径下的所有资源都需要身份认证后才能访问
                .antMatchers("/r/**").authenticated()
                // 其他所有请求都可以访问
                .anyRequest().permitAll()
                // 表单登录配置
                .and()
                .formLogin()
                // 自定义登录成功的页面地址
                .successForwardUrl("/login-success");
    }
}
