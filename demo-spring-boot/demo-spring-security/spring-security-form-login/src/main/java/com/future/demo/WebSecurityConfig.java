package com.future.demo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.annotation.Resource;
import java.util.Collections;

@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                // 禁用 CSRF 保护
                .csrf().disable()
                // 用于对 URL 进行访问权限控制
                .authorizeRequests()
                // /css/*.css资源允许匿名访问
                .antMatchers("/css/*.css").permitAll()
                // /login资源允许匿名访问
                .antMatchers("/login").permitAll()
                // 用于指定对于任何未明确指定权限要求的请求（即前面未通过 .antMatchers() 等方法明确匹配的请求），都需要用户进行身份验证（即用户必须登录）。
                .anyRequest().authenticated()
                .and()
                // 指定form登录界面 URL
                .formLogin().loginPage("/login")
                // 指定form登录成功后跳转的 URL
                .defaultSuccessUrl("/welcome")
                // 会话管理配置
                .and()
                // 指定会话创建策略，这里是 IF_REQUIRED 表示只有在需要时才创建会话
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED);
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new UserDetailService();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        // 自定义用户信息数据源，提供用户信息给验证框架校验
        auth.userDetailsService(userDetailsService());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    public static class UserDetailService implements UserDetailsService {
        @Resource
        private PasswordEncoder passwordEncoder;

        @Override
        public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
            String password = this.passwordEncoder.encode("1234567");
            return new User(username, password, Collections.emptyList());
        }
    }
}