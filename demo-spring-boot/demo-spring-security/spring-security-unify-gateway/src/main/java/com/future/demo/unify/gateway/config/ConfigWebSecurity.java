package com.future.demo.unify.gateway.config;

import com.future.demo.unify.gateway.common.MyAccessDeniedHandler;
import com.future.demo.unify.gateway.common.MyAuthenticationEntryPoint;
import com.future.demo.unify.gateway.common.MyLogoutSuccessHandler;
import com.future.demo.unify.gateway.common.TokenAuthenticationFilter;
import com.future.demo.unify.gateway.password.*;
import com.future.demo.unify.gateway.sms.*;
import net.sf.ehcache.CacheManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.annotation.Resource;
import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class ConfigWebSecurity extends WebSecurityConfigurerAdapter {
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    SmsCaptchaUserDetailsService smsCaptchaUserDetailsService;
    @Autowired
    SmsCaptchaAuthenticationSuccessHandler smsCaptchaAuthenticationSuccessHandler;
    @Autowired
    SmsCaptchaAuthenticationFailureHandler smsCaptchaAuthenticationFailureHandler;
    @Autowired
    SmsCaptchaAuthenticationProvider smsCaptchaAuthenticationProvider;

    @Autowired
    CustomizePasswordUserDetailsService customizePasswordUserDetailsService;
    @Autowired
    CustomizePasswordAuthenticationSuccessHandler customizePasswordAuthenticationSuccessHandler;
    @Autowired
    CustomizePasswordAuthenticationFailureHandler customizePasswordAuthenticationFailureHandler;
    @Autowired
    CustomizePasswordAuthenticationProvider customizePasswordAuthenticationProvider;

    @Autowired
    MyLogoutSuccessHandler myLogoutSuccessHandler;
    @Autowired
    MyAccessDeniedHandler myAccessDeniedHandler;
    @Resource
    MyAuthenticationEntryPoint myAuthenticationEntryPoint;

    @Autowired
    TokenAuthenticationFilter tokenAuthenticationFilter;

    // todo 为何下面不能够注释
    // 这个接口专门用于配置系统默认的UsernamePasswordAuthentication
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(customizePasswordUserDetailsService);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // 配置手机号码+短信验证码登录
        SmsCaptchaAuthenticationFilter smsCaptchaAuthenticationFilter = new SmsCaptchaAuthenticationFilter();
        smsCaptchaAuthenticationFilter.setAuthenticationManager(authenticationManager);
        smsCaptchaAuthenticationFilter.setAuthenticationSuccessHandler(smsCaptchaAuthenticationSuccessHandler);
        smsCaptchaAuthenticationFilter.setAuthenticationFailureHandler(smsCaptchaAuthenticationFailureHandler);

        CustomizePasswordAuthenticationFilter customizePasswordAuthenticationFilter = new CustomizePasswordAuthenticationFilter();
        customizePasswordAuthenticationFilter.setAuthenticationManager(authenticationManager);
        customizePasswordAuthenticationFilter.setAuthenticationSuccessHandler(customizePasswordAuthenticationSuccessHandler);
        customizePasswordAuthenticationFilter.setAuthenticationFailureHandler(customizePasswordAuthenticationFailureHandler);

        http
                .csrf().disable()

                // 禁用session
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                // token验证filter
                .and().addFilterBefore(tokenAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)

                // 未登录异常处理
                .exceptionHandling()
                .accessDeniedHandler(myAccessDeniedHandler)
                .authenticationEntryPoint(myAuthenticationEntryPoint)

                // 登出配置
                .and().logout()
                .logoutUrl("/api/v1/logout")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .logoutSuccessHandler(myLogoutSuccessHandler)

                // 接口权限
                .and()
                .authorizeRequests().antMatchers("/api/v1/user/test2").hasRole("admin")
                .antMatchers("/api/v1/user/test3").hasAuthority("user:creation")

                // 允许用户名、手机号码、邮箱+密码登录url
                .and().authorizeRequests().antMatchers("/api/v1/password/login").permitAll()

                // 模拟用户名、手机号码、邮箱+密码尝试多次登录失败后需要提供登录验证码才能够继续登录系统
                .and().authenticationProvider(customizePasswordAuthenticationProvider)
                .addFilterBefore(customizePasswordAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)

                // form login配置
                .formLogin().disable()
//                .loginProcessingUrl("/api/v1/password/login")
//                .failureHandler(usernamePasswordAuthenticationFailureHandler)
//                .successHandler(usernamePasswordAuthenticationSuccessHandler)

//                .and()
                .authorizeRequests().antMatchers("/api/v1/password/captcha/get").permitAll()

                // 手机号码+短信验证码登录时发送短信验证码
                .and().authorizeRequests().antMatchers("/api/v1/sms/captcha/send").permitAll()

                // 配置手机号码+短信验证码登录
                .and().authorizeRequests().antMatchers("/api/v1/sms/login").permitAll()
                .and().authenticationProvider(smsCaptchaAuthenticationProvider)
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