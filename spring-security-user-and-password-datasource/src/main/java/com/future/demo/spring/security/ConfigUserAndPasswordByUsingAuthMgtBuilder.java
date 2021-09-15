//package com.future.demo.spring.security;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//
//@Configuration
//public class ConfigUserAndPasswordByUsingAuthMgtBuilder extends WebSecurityConfigurerAdapter {
//    @Autowired
//    PasswordEncoder passwordEncoder;
//
//    @Bean
//    PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//
//    @Override
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        // 使用配置类配置用户密码
//        auth.inMemoryAuthentication()
//                .withUser("user2").password(passwordEncoder.encode("123456")).roles("role2");
//    }
//}