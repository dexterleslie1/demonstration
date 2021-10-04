package com.future.demo.unify.gateway;

import com.future.demo.unify.gateway.password.UsernamePasswordLoginCaptchaFilter;
import com.future.demo.unify.gateway.sms.SmsCaptchaSendFilter;
import com.future.demo.unify.gateway.password.UsernamePasswordUserDetailsService;
import com.future.demo.unify.gateway.sms.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

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
    UsernamePasswordUserDetailsService usernamePasswordUserDetailsService;

    // 这个接口专门用于配置系统默认的UsernamePasswordAuthentication
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(usernamePasswordUserDetailsService);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // 配置手机号码+短信验证码登录
        SmsCaptchaAuthenticationFilter smsCaptchaAuthenticationFilter = new SmsCaptchaAuthenticationFilter();
        smsCaptchaAuthenticationFilter.setAuthenticationManager(authenticationManager);
        smsCaptchaAuthenticationFilter.setAuthenticationSuccessHandler(smsCaptchaAuthenticationSuccessHandler);
        smsCaptchaAuthenticationFilter.setAuthenticationFailureHandler(smsCaptchaAuthenticationFailureHandler);

        SmsCaptchaAuthenticationProvider smsCaptchaAuthenticationProvider = new SmsCaptchaAuthenticationProvider();
        smsCaptchaAuthenticationProvider.setUserDetailsService(smsCaptchaUserDetailsService);

        http
                .csrf().disable()

                // form login配置
                .formLogin()

                // /api/v1/captcha/get使用CaptchaFilter处理并且不需要登录
                // TODO 怎么限制CaptchaFilter只能使用GET方法请求
                .and().authorizeRequests().antMatchers("/api/v1/captcha/get").permitAll()

                .and().addFilterBefore(new UsernamePasswordLoginCaptchaFilter(), UsernamePasswordAuthenticationFilter.class)

                // 手机号码+短信验证码登录时发送短信验证码
                // TODO 怎么现在SmsCaptchaSendFilter只能使用POST方法请求
                .authorizeRequests().antMatchers("/api/v1/captcha/sms/send").permitAll()
                .and().addFilterBefore(new SmsCaptchaSendFilter(), UsernamePasswordAuthenticationFilter.class)

                // 配置手机号码+短信验证码登录
                .authorizeRequests().antMatchers("/api/v1/sms/login").permitAll()
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