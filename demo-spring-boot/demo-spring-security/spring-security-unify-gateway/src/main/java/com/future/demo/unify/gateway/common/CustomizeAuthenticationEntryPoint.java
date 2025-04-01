package com.future.demo.unify.gateway.common;

import com.future.common.constant.ErrorCodeConstant;
import com.future.common.http.ResponseUtils;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 未登录时处理
 */
@Component
public class CustomizeAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        ResponseUtils.writeFailResponse(response, HttpServletResponse.SC_UNAUTHORIZED, ErrorCodeConstant.ErrorCodeLoginRequired, "未登录");
    }
}
