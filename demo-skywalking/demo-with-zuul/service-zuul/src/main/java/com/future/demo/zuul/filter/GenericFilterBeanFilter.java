package com.future.demo.zuul.filter;

import lombok.extern.slf4j.Slf4j;
import org.apache.skywalking.apm.toolkit.trace.TraceContext;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class GenericFilterBeanFilter extends GenericFilterBean {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String uri=request.getRequestURI();
        log.debug("请求前uri={}", uri);

        String traceId = TraceContext.traceId();
        if (servletResponse instanceof HttpServletResponse) {
            ((HttpServletResponse)servletResponse).addHeader("x-request-id", traceId);
        }

        chain.doFilter(servletRequest, servletResponse);
    }

}
