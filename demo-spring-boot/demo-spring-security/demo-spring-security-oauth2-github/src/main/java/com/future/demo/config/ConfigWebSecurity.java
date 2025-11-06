package com.future.demo.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Configuration
@Slf4j
public class ConfigWebSecurity extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                // /css/*.css资源允许匿名访问
                .antMatchers("/css/*.css").permitAll()
                .antMatchers("/").permitAll()
                .anyRequest().authenticated()

                // 启用OAuth2.0登录以支持Github登录集成
                .and().oauth2Login().successHandler(new AuthenticationSuccessHandler() {
                    @Override
                    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
                        // 模拟OAuth2 Github登录成功但自定义登录处理逻辑异常情况
                        /*boolean b = true;
                        if (b) {
                            SecurityContextHolder.getContext().setAuthentication(null);
                            response.sendRedirect("/?error=" + URLEncoder.encode("自定义登录处理逻辑异常", StandardCharsets.UTF_8.name()));
                            return;
                        }*/

                        // 把OAuth2AuthenticationToken替换为CustomizeAuthenticationToken
                        handleCustomAuthenticationToken(authentication);

                        // 登录成功后跳转到/welcome页面
                        response.sendRedirect("/welcome");
                    }
                }).failureHandler(new AuthenticationFailureHandler() {
                    @Override
                    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
                        // Github授权后回调http://localhost:8080/login/oauth2/code/github?code=xxx&state=xxx时提供错误的code参数会触发此异常
                        log.error(exception.getMessage(), exception);
                        // 跳转/?error=错误信息
                        response.sendRedirect("/?error=" + URLEncoder.encode(exception.getMessage(), StandardCharsets.UTF_8.name()));
                    }
                });
    }

    private void handleCustomAuthenticationToken(Authentication authentication) {
        if (authentication instanceof OAuth2AuthenticationToken) {
            OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;

            // 提示：可以在这里结合业务需求添加自定义处理逻辑

            CustomizeAuthenticationToken customToken = new CustomizeAuthenticationToken(
                    oauthToken.getPrincipal(),
                    oauthToken.getAuthorities(),
                    oauthToken.getAuthorizedClientRegistrationId()
            );
            // 把OAuth2AuthenticationToken替换为CustomizeAuthenticationToken
            SecurityContextHolder.getContext().setAuthentication(customToken);
        }
    }
}
