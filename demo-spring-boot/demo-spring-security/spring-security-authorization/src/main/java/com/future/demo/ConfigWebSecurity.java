package com.future.demo;

import com.future.common.http.ObjectResponse;
import com.future.common.http.ResponseUtils;
import com.future.common.json.JSONUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
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
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * @author Dexterleslie.Chan
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(
        securedEnabled = true,/* 开启secured注解判断是否拥有角色 */
        prePostEnabled = true/* 开启preAuthorize注解 */)
public class ConfigWebSecurity extends WebSecurityConfigurerAdapter {

    @Resource
    TokenAuthenticationFilter tokenAuthenticationFilter;
    @Resource
    TokenStore tokenStore;
    @Resource
    PasswordEncoder passwordEncoder;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                .and().addFilterBefore(tokenAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)

                .logout()
                .logoutUrl("/api/auth/logout")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .logoutSuccessHandler(logoutSuccessHandler())

                .and()
                .exceptionHandling()
                .accessDeniedHandler(accessDeniedHandler())
                .authenticationEntryPoint(authenticationEntryPoint())

                .and()
                .authorizeRequests()
                // 同时拥有r1和r2角色的用户都可以调用次方法。
                .antMatchers("/api/v1/test3").access("hasRole('r1') and hasRole('r2')")
                // 拥有权限 auth:test5才能调用此方法。
                .antMatchers("/api/v1/test5").hasAuthority("perm:test5")
                .antMatchers("/api/auth/login").permitAll()
                .anyRequest().authenticated()

                .and()
                .formLogin()
                .loginProcessingUrl("/api/auth/login")
                .successHandler(authenticationSuccessHandler())
                .failureHandler(authenticationFailureHandler());
    }

    AuthenticationSuccessHandler authenticationSuccessHandler() {
        return new AuthenticationSuccessHandler() {
            @Override
            public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
                String token = UUID.randomUUID().toString();
                Map<String, Object> mapReturn = new HashMap<>();
                mapReturn.put("loginname", authentication.getName());
                mapReturn.put("token", token);
                ObjectResponse<Map<String, Object>> responseO = ResponseUtils.successObject(mapReturn);
                ConfigWebSecurity.this.tokenStore.store(token, (MyUser) authentication.getPrincipal());
                response.setCharacterEncoding(StandardCharsets.UTF_8.name());
                response.getWriter().write(JSONUtil.ObjectMapperInstance.writeValueAsString(responseO));
            }
        };
    }

    AuthenticationFailureHandler authenticationFailureHandler() {
        return new AuthenticationFailureHandler() {
            @Override
            public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                ObjectResponse<String> responseO = ResponseUtils.failObject(50000, exception.getMessage());
                response.setCharacterEncoding(StandardCharsets.UTF_8.name());
                response.getWriter().write(JSONUtil.ObjectMapperInstance.writeValueAsString(responseO));
            }
        };
    }

    LogoutSuccessHandler logoutSuccessHandler() {
        return new LogoutSuccessHandler() {
            @Override
            public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
                ObjectResponse<String> responseO = ResponseUtils.successObject("成功退出");
                response.setCharacterEncoding(StandardCharsets.UTF_8.name());
                response.getWriter().write(JSONUtil.ObjectMapperInstance.writeValueAsString(responseO));
            }
        };
    }

    AccessDeniedHandler accessDeniedHandler() {
        return new AccessDeniedHandler() {
            @Override
            public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                ObjectResponse<String> responseO = ResponseUtils.failObject(50002, "权限不足");
                response.setCharacterEncoding(StandardCharsets.UTF_8.name());
                response.getWriter().write(JSONUtil.ObjectMapperInstance.writeValueAsString(responseO));
            }
        };
    }

    AuthenticationEntryPoint authenticationEntryPoint() {
        return new AuthenticationEntryPoint() {
            @Override
            public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                ObjectResponse<String> responseO = ResponseUtils.failObject(50001, "您未登陆");
                response.setCharacterEncoding(StandardCharsets.UTF_8.name());
                response.getWriter().write(JSONUtil.ObjectMapperInstance.writeValueAsString(responseO));
            }
        };
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(new UserDetailsService() {
            @Override
            public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                // 测试@Secured注解
                if (username.equals("user-with-role-r1-and-role-r2")) {
                    return new MyUser(username,
                            passwordEncoder.encode("123456"),
                            Arrays.asList(new SimpleGrantedAuthority("ROLE_r1"),
                                    new SimpleGrantedAuthority("ROLE_r2")));
                } else if (username.equals("user-with-only-role-r1")) {
                    return new MyUser(username,
                            passwordEncoder.encode("123456"),
                            Collections.singletonList(new SimpleGrantedAuthority("ROLE_r1")));
                } else if (username.equals("user-with-only-role-r2")) {
                    return new MyUser(username,
                            passwordEncoder.encode("123456"),
                            Collections.singletonList(new SimpleGrantedAuthority("ROLE_r2")));
                } else if (username.equals("user-with-none-role")) {
                    return new MyUser(username,
                            passwordEncoder.encode("123456"),
                            new ArrayList<>());
                } else if (username.equals("user-with-perm-test5")) {
                    return new MyUser(username,
                            passwordEncoder.encode("123456"),
                            Collections.singletonList(new SimpleGrantedAuthority("perm:test5")));
                } else if (username.equals("user-with-allow-uri-test6")) {
                    return new MyUser(username,
                            passwordEncoder.encode("123456"),
                            Collections.singletonList(new SimpleGrantedAuthority("/api/v1/test6")));
                } else {
                    return new MyUser(username,
                            passwordEncoder.encode("123456"),
                            new ArrayList<>());
                }
            }
        });
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}