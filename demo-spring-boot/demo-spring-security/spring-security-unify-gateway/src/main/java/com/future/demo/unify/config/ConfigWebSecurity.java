package com.future.demo.unify.config;


import com.future.demo.unify.common.*;
import com.future.demo.unify.email.UnifyEmailAuthenticationProvider;
import com.future.demo.unify.password.UnifyPasswordAuthenticationProvider;
import com.future.demo.unify.sms.UnifySmsCaptchaAuthenticationProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.annotation.Resource;

@Configuration
@EnableWebSecurity
// 开启preAuthorize注解
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class ConfigWebSecurity extends WebSecurityConfigurerAdapter {

    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    CustomizeAuthenticationSuccessHandler customizeAuthenticationSuccessHandler;
    @Autowired
    CustomizeAuthenticationFailureHandler customizeAuthenticationFailureHandler;

    @Autowired
    UnifyPasswordAuthenticationProvider unifyPasswordAuthenticationProvider;
    @Autowired
    UnifySmsCaptchaAuthenticationProvider unifySmsCaptchaAuthenticationProvider;
    @Autowired
    UnifyEmailAuthenticationProvider unifyEmailAuthenticationProvider;

    @Autowired
    CustomizeLogoutSuccessHandler customizeLogoutSuccessHandler;
    @Autowired
    CustomizeAccessDeniedHandler customizeAccessDeniedHandler;
    @Resource
    CustomizeAuthenticationEntryPoint customizeAuthenticationEntryPoint;

    @Autowired
    CustomizeTokenAuthenticationFilter customizeTokenAuthenticationFilter;

    // 注意：不能省略此配置，否则在AuthenticationProvider中抛出BadCredentialsException时登录流程会死循环
    // 这个接口专门用于配置系统UsernamePasswordAuthenticationFilter中默认的UserDetailsService
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(new UserDetailsService() {
            @Override
            public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                return null;
            }
        });
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        UnifyAuthenticationFilter unifyAuthenticationFilter = new UnifyAuthenticationFilter();
        unifyAuthenticationFilter.setAuthenticationManager(authenticationManager);
        unifyAuthenticationFilter.setAuthenticationSuccessHandler(customizeAuthenticationSuccessHandler);
        unifyAuthenticationFilter.setAuthenticationFailureHandler(customizeAuthenticationFailureHandler);

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
                .logoutUrl("/api/v1/auth/logout")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .logoutSuccessHandler(customizeLogoutSuccessHandler)

                // 接口权限
                .and()
                .authorizeRequests()

                .and()
                // form login配置
                .formLogin().disable()

                // 允许密码登录是获取验证码
                .authorizeRequests().antMatchers("/api/v1/auth/getCaptcha").permitAll()
                // 允许短信验登录时发送短信验证码
                .and().authorizeRequests().antMatchers("/api/v1/auth/sendSms").permitAll()

                // 登录url
                .and().authorizeRequests().antMatchers("/api/v1/auth/login").permitAll()
                .and().authenticationProvider(unifyPasswordAuthenticationProvider)
                .authenticationProvider(unifySmsCaptchaAuthenticationProvider)
                .authenticationProvider(unifyEmailAuthenticationProvider)
                .addFilterBefore(unifyAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)

                .authorizeRequests().anyRequest().authenticated();
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    // 提示：独立配置PasswordEncoder是为了避免启动应用时提示循环依赖错误
    @Configuration
    public static class ConfigPasswordEncoder {
        @Bean(name = "bCryptPasswordEncoder")
        public PasswordEncoder bCryptPasswordEncoder() {
            return new BCryptPasswordEncoder();
        }
    }
}