package com.future.demo.unify.common;

import cn.hutool.extra.servlet.ServletUtil;
import cn.hutool.json.JSONConfig;
import cn.hutool.json.JSONUtil;
import com.future.common.constant.ErrorCodeConstant;
import com.future.common.http.ObjectResponse;
import com.future.common.http.ResponseUtils;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 未登录时处理
 */
@Component
public class CustomizeAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        ObjectResponse<String> objectResponse = ResponseUtils.failObject(ErrorCodeConstant.ErrorCodeLoginRequired, "未登录");
        String json = JSONUtil.toJsonStr(objectResponse, JSONConfig.create().setIgnoreNullValue(false));
        ServletUtil.write(response, json, MediaType.APPLICATION_JSON_UTF8_VALUE);
    }
}
