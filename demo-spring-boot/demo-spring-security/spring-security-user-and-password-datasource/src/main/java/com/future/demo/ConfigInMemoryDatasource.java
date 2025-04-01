//package com.future.demo.spring.security;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
//import org.springframework.security.crypto.password.PasswordEncoder;
//
//@Configuration
//public class ConfigInMemoryDatasource extends WebSecurityConfigurerAdapter {
//    @Autowired
//    PasswordEncoder passwordEncoder;
//
//    @Override
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        // 使用配置类配置用户密码
//        // 用户认证信息在内存中临时存放
//        auth.inMemoryAuthentication()
//                .withUser("user2").password(passwordEncoder.encode("123456")).roles("role2");
//    }
//}