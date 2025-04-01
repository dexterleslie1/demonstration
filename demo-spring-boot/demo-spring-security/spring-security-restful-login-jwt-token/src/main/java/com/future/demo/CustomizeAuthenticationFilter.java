package com.future.demo;

import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.future.common.constant.ErrorCodeConstant;
import com.future.common.http.ResponseUtils;
import com.future.common.jwt.JwtUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// 验证用户是否登录拦截器
@Component
public class CustomizeAuthenticationFilter extends OncePerRequestFilter {
    @Value("${customize.publicKey}")
    private String publicKey;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getServletPath();
        // 不拦截登录接口
        return "/api/v1/auth/login".equalsIgnoreCase(path);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            // 获取请求中携带的jwt token并解码，
            // 解码成功后则构造Authentication对象并注入到请求上下文中
            String jwtToken = obtainBearerToken(request);
            if (!StringUtils.isBlank(jwtToken)) {
                DecodedJWT decodedJWT = JwtUtil.verifyWithPublicKey(publicKey, jwtToken);
                Long userId = decodedJWT.getClaim("userId").asLong();
                List<String> roleList = decodedJWT.getClaim("roleList").asList(String.class);
                List<GrantedAuthority> authorityList = new ArrayList<>();
                if (roleList != null) {
                    roleList.forEach(o -> {
                        authorityList.add(new SimpleGrantedAuthority(o));
                    });
                }
                List<String> permissionList = decodedJWT.getClaim("permissionList").asList(String.class);
                if (permissionList != null) {
                    permissionList.forEach(o -> {
                        authorityList.add(new SimpleGrantedAuthority(o));
                    });
                }

                CustomizeUser user = new CustomizeUser(userId, authorityList);
                CustomizeAuthentication authentication = new CustomizeAuthentication(user);
                authentication.setAuthenticated(true);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
            filterChain.doFilter(request, response);
        } catch (Exception ex) {
            if (ex instanceof TokenExpiredException) {
                // jwt token过期
                ResponseUtils.writeFailResponse(response, ErrorCodeConstant.ErrorCodeTokenExpired, "token已过期！");
            } else if (ex instanceof JWTDecodeException) {
                // 非法jwt token
                ResponseUtils.writeFailResponse(response, ErrorCodeConstant.ErrorCodeCommon, "您未登陆");
            } else {
                logger.error(ex.getMessage(), ex);
                ResponseUtils.writeFailResponse(response, ErrorCodeConstant.ErrorCodeCommon, "网络繁忙，稍后重试！");
            }
        }
    }

    String obtainBearerToken(HttpServletRequest request) {
        String bearerStr = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.isBlank(bearerStr)) {
            return bearerStr;
        }

        return bearerStr.replace("Bearer ", "");
    }
}
