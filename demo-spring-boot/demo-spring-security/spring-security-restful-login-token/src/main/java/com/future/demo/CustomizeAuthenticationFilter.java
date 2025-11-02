package com.future.demo;

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

// 验证用户是否登录拦截器
@Component
public class CustomizeAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    TokenStore tokenStore;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        // 拦截所有请求
        return false;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        // 获取请求中携带的token并在本地查询是否有此token，
        // 是，则构造Authentication对象并注入到请求上下文中
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
