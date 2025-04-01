package com.future.demo.unify.gateway.common;

import com.future.common.constant.ErrorCodeConstant;
import com.future.common.http.ResponseUtils;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 权限不足时处理
 */
@Component
public class CustomizeAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {
        ResponseUtils.writeFailResponse(response, HttpServletResponse.SC_FORBIDDEN, ErrorCodeConstant.ErrorCodeCommon, "权限不足");
    }
}
