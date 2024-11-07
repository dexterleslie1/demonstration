package com.future.demo.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.URI;

/**
 * 局部filter
 */
@Slf4j
@Component
public class MyGatewayFilterFactory extends AbstractGatewayFilterFactory<Object> {

    @Override
    public GatewayFilter apply(Object config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            String hostname = null;
            try {
                hostname = InetAddress.getLocalHost().getHostName();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            request = request.mutate()
                    .uri(URI.create("/api/v1/test1?hostnameGateway=" + hostname))
                    .build();
            return chain.filter(exchange.mutate().request(request).build());
        };
    }
}
