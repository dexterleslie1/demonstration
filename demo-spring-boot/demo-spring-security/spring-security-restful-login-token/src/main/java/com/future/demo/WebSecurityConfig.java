package com.future.demo;

import com.future.common.constant.ErrorCodeConstant;
import com.future.common.http.RequestUtils;
import com.future.common.http.ResponseUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author Dexterleslie.Chan
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Resource
    CustomizeAuthenticationFilter customizeAuthenticationFilter;
    @Resource
    TokenStore tokenStore;
    @Resource
    PasswordEncoder passwordEncoder;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                // 禁用 CSRF 保护
                .csrf().disable()
                // 用于指示Spring Security不应为客户端创建HTTP会话（即，不应在服务器上存储会话数据）。当您将此策略设置为无状态时，Spring Security将不会使用HTTP会话来跟踪用户身份或状态。
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                // 验证用户是否登录拦截器
                .and().addFilterBefore(customizeAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)

                // 退出设置
                .logout()
                // 退出URL，自定义的所有OncePerRequestFilter都不会拦截这个URL
                .logoutUrl("/api/auth/logout")
                // 退出时清除session
                .invalidateHttpSession(true)
                // 退出时清除cookie
                .deleteCookies("JSESSIONID")
                // 退出成功时处理
                .logoutSuccessHandler(logoutSuccessHandler())

                .and()
                // 异常处理设置
                .exceptionHandling()
                // 权限不足时处理
                .accessDeniedHandler(accessDeniedHandler())
                // 未登录时处理，即SecurityContextHolder中不存在Authentication对象时
                .authenticationEntryPoint(authenticationEntryPoint())

                .and()
                // 用于对 URL 进行访问权限控制
                .authorizeRequests()
                // 登录接口允许匿名访问
                .antMatchers("/api/auth/login").permitAll()
                // 访问资源需要USER角色
                .antMatchers("/api/auth/a1").hasRole("USER")
                // 访问资源需要USER1角色
                .antMatchers("/api/auth/a2").hasRole("USER1")
                // 用于指定对于任何未明确指定权限要求的请求（即前面未通过 .antMatchers() 等方法明确匹配的请求），都需要用户进行身份验证（即用户必须登录）。
                .anyRequest().authenticated()

                .and()
                // form登录设置
                .formLogin()
                // 登录URL
                .loginProcessingUrl("/api/auth/login")
                // 登录成功时处理
                .successHandler(authenticationSuccessHandler())
                // 登录失败时处理
                .failureHandler(authenticationFailureHandler());
    }

    AuthenticationSuccessHandler authenticationSuccessHandler() {
        return new AuthenticationSuccessHandler() {
            @Override
            public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
                Long userId = ((CustomizeUserDetails) (authentication).getPrincipal()).getUserId();
                String token = UUID.randomUUID().toString();
                Map<String, Object> mapReturn = new HashMap<>();
                mapReturn.put("userId", userId);
                mapReturn.put("loginname", authentication.getName());
                mapReturn.put("token", token);
                CustomizeUser customizeUser = new CustomizeUser(userId, ((CustomizeUserDetails) authentication.getPrincipal()).getAuthorities());
                WebSecurityConfig.this.tokenStore.store(token, customizeUser);
                ResponseUtils.writeSuccessResponse(response, mapReturn);
            }
        };
    }

    AuthenticationFailureHandler authenticationFailureHandler() {
        return new AuthenticationFailureHandler() {
            @Override
            public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
                ResponseUtils.writeFailResponse(response, HttpServletResponse.SC_UNAUTHORIZED, ErrorCodeConstant.ErrorCodeCommon, exception.getMessage());
            }
        };
    }

    LogoutSuccessHandler logoutSuccessHandler() {
        return new LogoutSuccessHandler() {
            @Override
            public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
                String token = RequestUtils.ObtainBearerToken(request);
                WebSecurityConfig.this.tokenStore.remove(token);
                ResponseUtils.writeSuccessResponse(response, "成功退出");
            }
        };
    }

    AccessDeniedHandler accessDeniedHandler() {
        return new AccessDeniedHandler() {
            @Override
            public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
                ResponseUtils.writeFailResponse(response, HttpServletResponse.SC_FORBIDDEN, ErrorCodeConstant.ErrorCodeCommon, "权限不足");
            }
        };
    }

    AuthenticationEntryPoint authenticationEntryPoint() {
        return new AuthenticationEntryPoint() {
            @Override
            public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
                ResponseUtils.writeFailResponse(response, HttpServletResponse.SC_UNAUTHORIZED, ErrorCodeConstant.ErrorCodeCommon, "您未登陆");
            }
        };
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(new UserDetailsService() {
            @Override
            public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                Long userId = 4738438L;
                return new CustomizeUserDetails(userId, username, passwordEncoder.encode("123456"), Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
            }
        });
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}