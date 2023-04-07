package com.future.demo.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 全局filter
 */
@Slf4j
@Component
public class MyGlobalFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String uri = exchange.getRequest().getURI().getPath();
        log.debug("请求前uri={},params={}", uri, exchange.getRequest().getQueryParams());
        Mono<Void> mono = chain.filter(exchange);
        log.debug("请求后response={}", exchange.getResponse());
        return mono;
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
