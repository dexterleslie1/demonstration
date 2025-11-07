package com.future.demo.security.uaa.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.annotation.Resource;

@Configuration
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
        // 禁用csrf
        /*http.csrf().disable()
                // 开启form登录
                .formLogin()
                // 所有请求都需要登录验证
                .and().authorizeRequests().anyRequest().authenticated();*/

        http
                // 禁用csrf
                .csrf().disable()

                // 用于对 URL 进行访问权限控制
                .authorizeRequests()
                // /css/*.css资源允许匿名访问
                .antMatchers("/css/*.css").permitAll()
                // /login资源允许匿名访问
                .antMatchers("/login").permitAll()
                // 用于指定对于任何未明确指定权限要求的请求（即前面未通过 .antMatchers() 等方法明确匹配的请求），都需要用户进行身份验证（即用户必须登录）。
                .anyRequest().authenticated()

                // 开启form登录
                .and().formLogin()// 自定义登录表单username、password参数名称
                .usernameParameter("usernameDemo")
                .passwordParameter("passwordDemo")
                // 自定义登录页面
                .loginPage("/login")
                // 自定义登录url，此值要和login.html登录表单action一致
                .loginProcessingUrl("/myLogin");
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
