package com.future.demo.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.apache.skywalking.apm.toolkit.trace.TraceContext;
import org.springframework.cloud.gateway.filter.headers.HttpHeadersFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

/**
 * 设置skywalking的traceId到 http response 请求头
 */
@Slf4j
@Component
public class TraceResponseFilter implements HttpHeadersFilter {

    @Override
    public HttpHeaders filter(HttpHeaders input, ServerWebExchange exchange) {
        String traceId = TraceContext.traceId();
        exchange.getResponse().getHeaders().add("x-ecommerce-request-id", traceId);
        return input;
    }

}