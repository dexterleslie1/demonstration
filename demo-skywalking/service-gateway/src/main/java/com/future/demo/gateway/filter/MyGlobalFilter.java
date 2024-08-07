package com.future.demo.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.apache.skywalking.apm.toolkit.trace.TraceContext;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * 全局filter
 */
@Slf4j
@Component
public class MyGlobalFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request =  exchange.getRequest();
        String uri = request.getURI().getPath();
        log.debug("请求前uri={},params={}", uri, request.getQueryParams());
        Mono<Void> mono = chain.filter(exchange);
        log.debug("请求后response={}", exchange.getResponse());
        return mono;
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
