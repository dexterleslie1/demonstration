package com.future.demo.security.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Service;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

@Configuration
public class Config extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // 禁用csrf
        http.csrf().disable();

        http.authorizeRequests()
                // 不拦截login.html页面请求
                .antMatchers("/login.html").permitAll()
                // 不拦截loginFailed.html页面请求
                .antMatchers("/loginFailed.html").permitAll()
                // 拦截所有未登录url
                .anyRequest().authenticated();

        http.formLogin()
                // 自定义登录表单username、password参数名称
                .usernameParameter("usernameDemo")
                .passwordParameter("passwordDemo")
                // 自定义登录页面
                .loginPage("/login.html")
                // 自定义登录url，此值要和login.html登录表单action一致
                .loginProcessingUrl("/login")
                // 登录成功后跳转url，最终会跳转到index.html
                .successForwardUrl("/index")
//                // 自定义登录成功处理器
//                .successHandler(new AuthenticationSuccessHandler() {
//                    @Override
//                    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
//                        response.sendRedirect("https://www.baidu.com");
//                    }
//                })
                // 登录失败跳转url，最终会跳转到loginFailed.html
                .failureForwardUrl("/loginFailed");
    }

    // 密码编码器
    @Bean
    BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Service
    public static class UserDetailsServiceImpl implements UserDetailsService {

        @Autowired
        PasswordEncoder passwordEncoder;

        @Override
        public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
            if (!"admin".equals(username)) {
                throw new UsernameNotFoundException("用户" + username + "不存在");
            }

            String encodedPassword = passwordEncoder.encode("123");
            return new User(username, encodedPassword, Collections.emptyList());
        }
    }
}