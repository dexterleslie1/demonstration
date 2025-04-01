package com.future.demo;

import org.springframework.web.filter.CommonsRequestLoggingFilter;

import javax.servlet.http.HttpServletRequest;

public class CustomizeCommonsRequestLoggingFilter extends CommonsRequestLoggingFilter {
    @Override
    protected boolean shouldLog(HttpServletRequest request) {
        String uri = request.getRequestURI();
        // swagger2请求不记录日志
        return !uri.contains("swagger") && !uri.contains("api-docs");
    }
}
