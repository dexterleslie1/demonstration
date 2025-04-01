package com.future.demo;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class TokenAuthenticationFilter extends OncePerRequestFilter {
    @Autowired
    TokenStore tokenStore;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getServletPath();
        return "/api/v1/auth/login".equalsIgnoreCase(path);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String token = obtainBearerToken(request);
        if(!StringUtils.isBlank(token)) {
            MyUser user = tokenStore.get(token);

            if (user != null) {
                MyAuthentication authentication = new MyAuthentication(user);
                authentication.setAuthenticated(true);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        filterChain.doFilter(request, response);
    }

    String obtainBearerToken(HttpServletRequest request) {
        String bearerStr = request.getHeader(HttpHeaders.AUTHORIZATION);
        if(StringUtils.isBlank(bearerStr)) {
            return bearerStr;
        }

        return bearerStr.replace("Bearer ", "");
    }
}
