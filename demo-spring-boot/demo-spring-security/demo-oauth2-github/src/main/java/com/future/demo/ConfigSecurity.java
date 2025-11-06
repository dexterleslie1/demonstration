package com.future.demo;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
public class ConfigSecurity extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                // 禁用 CSRF 保护
                .csrf().disable()

                // 用于对 URL 进行访问权限控制
                .authorizeRequests()
                // 下面资源允许匿名访问
                .antMatchers("/", "/github-callback", "/loginWithAuthorizationCode").permitAll()
                // 用于指定对于任何未明确指定权限要求的请求（即前面未通过 .antMatchers() 等方法明确匹配的请求），都需要用户进行身份验证（即用户必须登录）。
                .anyRequest().authenticated()

                .and()
                // 指定form登录界面 URL
                .formLogin()
                // 未登录用户跳转到首页
                .loginPage("/");
    }
}
