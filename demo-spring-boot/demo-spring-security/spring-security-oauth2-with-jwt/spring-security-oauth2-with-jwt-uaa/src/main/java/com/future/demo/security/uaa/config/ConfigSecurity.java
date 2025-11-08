package com.future.demo.security.uaa.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.annotation.Resource;

@Configuration
@EnableWebSecurity
public class ConfigSecurity extends WebSecurityConfigurerAdapter {

    // 所有用户密码
    private final static String UserSecret = "123456";

    @Bean
    public UserDetailsService users() {
        return new MyUserDetailsService();
    }

    public static class MyUserDetailsService implements UserDetailsService {
        @Resource
        private PasswordEncoder passwordEncoder;

        @Override
        public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
            if ("user1".equals(username)) {
                return User.builder()
                        .username("user1")
                        .password(passwordEncoder.encode(UserSecret))
                        .authorities("sys:admin")
                        .build();
            } else if ("user2".equals(username)) {
                return User.builder()
                        .username("user2")
                        .password(passwordEncoder.encode(UserSecret))
                        .authorities("sys:nothing")
                        .build();
            }
            return null;
        }
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .cors()
                // 禁用csrf
                .and().csrf().disable()
                // 开启form登录
                .formLogin()
                // 所有请求都需要登录验证
                .and().authorizeRequests()
                .anyRequest().authenticated();
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
