package com.future.demo.unify.gateway.common;

import com.future.common.http.RequestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 用户请求token拦截处理
 */
@Component
public class CustomizeTokenAuthenticationFilter extends OncePerRequestFilter {
    @Autowired
    TokenStore tokenStore;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return "/api/v1/sms/captcha/send".equalsIgnoreCase(path) || "/api/v1/sms/login".equalsIgnoreCase(path)
                || "/api/v1/password/login".equalsIgnoreCase(path) || "/api/v1/password/captcha/get".equalsIgnoreCase(path);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String token = RequestUtils.ObtainBearerToken(request);
        if (!StringUtils.isBlank(token)) {
            CustomizeUser user = tokenStore.get(token);
            if (user != null) {
                CustomizeAuthentication authentication = new CustomizeAuthentication(user);
                authentication.setAuthenticated(true);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        filterChain.doFilter(request, response);
    }
}
