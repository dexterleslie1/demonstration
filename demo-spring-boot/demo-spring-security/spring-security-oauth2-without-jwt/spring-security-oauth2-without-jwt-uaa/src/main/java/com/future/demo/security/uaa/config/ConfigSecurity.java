package com.future.demo.security.uaa.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
public class ConfigSecurity extends WebSecurityConfigurerAdapter {
    // 所有用户密码
    private final static String UserSecret = "123456";

    @Bean
    public UserDetailsService users() {
        UserDetails user1 = User.builder()
                .username("user1")
                .password(passwordEncoder().encode(UserSecret))
                .authorities("sys:admin")
                .build();
        UserDetails user2 = User.builder()
                .username("user2")
                .password(passwordEncoder().encode(UserSecret))
                .authorities("sys:nothing")
                .build();
        return new InMemoryUserDetailsManager(user1, user2);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // 禁用csrf
        http.csrf().disable();
        // 开启form登录
        http.formLogin();
        // 所有请求都需要登录验证
        http.authorizeRequests().anyRequest().authenticated();
    }

    // 密码模式，用户提供账号密码不需要登录直接校验通过并获取token
    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
