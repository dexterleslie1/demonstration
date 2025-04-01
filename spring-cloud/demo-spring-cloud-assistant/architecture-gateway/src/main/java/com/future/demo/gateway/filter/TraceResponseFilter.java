package com.future.demo.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.apache.skywalking.apm.toolkit.trace.TraceContext;
import org.apache.skywalking.apm.toolkit.webflux.WebFluxSkyWalkingOperators;
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
        // springcloud-gateway使用TraceContext.traceId()无法获取traceId
        String traceId = WebFluxSkyWalkingOperators.continueTracing(exchange, TraceContext::traceId);
        exchange.getResponse().getHeaders().add("x-request-id", traceId);
        return input;
    }

}