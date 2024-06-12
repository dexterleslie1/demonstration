package com.future.demo.unify.gateway.config;


import com.future.demo.unify.gateway.common.*;
import com.future.demo.unify.gateway.password.*;
import com.future.demo.unify.gateway.sms.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.annotation.Resource;

@Configuration
@EnableWebSecurity
public class ConfigWebSecurity extends WebSecurityConfigurerAdapter {
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    CustomizeAuthenticationSuccessHandler customizeAuthenticationSuccessHandler;
    @Autowired
    CustomizeAuthenticationFailureHandler customizeAuthenticationFailureHandler;

    @Autowired
    SmsCaptchaUserDetailsService smsCaptchaUserDetailsService;

    @Autowired
    SmsCaptchaAuthenticationProvider smsCaptchaAuthenticationProvider;

    @Autowired
    CustomizePasswordUserDetailsService customizePasswordUserDetailsService;
    @Autowired
    CustomizePasswordAuthenticationProvider customizePasswordAuthenticationProvider;

    @Autowired
    CustomizeLogoutSuccessHandler customizeLogoutSuccessHandler;
    @Autowired
    CustomizeAccessDeniedHandler customizeAccessDeniedHandler;
    @Resource
    CustomizeAuthenticationEntryPoint customizeAuthenticationEntryPoint;

    @Autowired
    CustomizeTokenAuthenticationFilter customizeTokenAuthenticationFilter;

    // 这个接口专门用于配置系统UsernamePasswordAuthenticationFilter中默认的UserDetailsService
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(customizePasswordUserDetailsService);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // 配置手机号码+短信验证码登录
        SmsCaptchaAuthenticationFilter smsCaptchaAuthenticationFilter = new SmsCaptchaAuthenticationFilter();
        smsCaptchaAuthenticationFilter.setAuthenticationManager(authenticationManager);
        smsCaptchaAuthenticationFilter.setAuthenticationSuccessHandler(customizeAuthenticationSuccessHandler);
        smsCaptchaAuthenticationFilter.setAuthenticationFailureHandler(customizeAuthenticationFailureHandler);

        // 配置密码登录
        CustomizePasswordAuthenticationFilter customizePasswordAuthenticationFilter = new CustomizePasswordAuthenticationFilter();
        customizePasswordAuthenticationFilter.setAuthenticationManager(authenticationManager);
        customizePasswordAuthenticationFilter.setAuthenticationSuccessHandler(customizeAuthenticationSuccessHandler);
        customizePasswordAuthenticationFilter.setAuthenticationFailureHandler(customizeAuthenticationFailureHandler);

        http
                .csrf().disable()

                // 禁用session
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                // token验证filter
                .and().addFilterBefore(customizeTokenAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)

                // 未登录异常处理
                .exceptionHandling()
                .accessDeniedHandler(customizeAccessDeniedHandler)
                .authenticationEntryPoint(customizeAuthenticationEntryPoint)

                // 登出配置
                .and().logout()
                .logoutUrl("/api/v1/logout")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .logoutSuccessHandler(customizeLogoutSuccessHandler)

                // 接口权限
                .and()
                .authorizeRequests()
                .antMatchers("/api/v1/user/test2").hasRole("admin")
                .antMatchers("/api/v1/user/test3").hasAuthority("user:creation")

                // 允许用户名、手机号码、邮箱+密码登录url
                .and().authorizeRequests().antMatchers("/api/v1/password/login").permitAll()
                // 添加密码登录AuthenticationProvider
                .and().authenticationProvider(customizePasswordAuthenticationProvider)
                // 密码登录AuthenticationFilter
                .addFilterBefore(customizePasswordAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)

                // form login配置
                .formLogin().disable()

                .authorizeRequests().antMatchers("/api/v1/password/captcha/get").permitAll()

                // 手机号码+短信验证码登录时发送短信验证码
                .and().authorizeRequests().antMatchers("/api/v1/sms/captcha/send").permitAll()

                // 配置手机号码+短信验证码登录
                .and().authorizeRequests().antMatchers("/api/v1/sms/login").permitAll()
                // 添加短信登录AuthenticationProvider
                .and().authenticationProvider(smsCaptchaAuthenticationProvider)
                // 密码登录AuthenticationFilter
                .addFilterBefore(smsCaptchaAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)

                .authorizeRequests().anyRequest().authenticated();
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean(name = "bCryptPasswordEncoder")
    public PasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}