package com.future.demo.gateway.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 自定义GlobalFilter作为权限验证
 * https://www.pianshen.com/article/4176276100/
 */
@Component
@Slf4j
public class TokenFilter implements GlobalFilter, Ordered {
    private final static ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 获取header中token参数
        List<String> tokenHeaderField = exchange.getRequest().getHeaders().get("token");
        String token = tokenHeaderField==null||tokenHeaderField.size()<=0?null:tokenHeaderField.get(0);
        if(StringUtils.isBlank(token)) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
            return exchange.getResponse().writeWith(Mono.create(monoSink -> {
                try {
                    Map<String, Object> mapResponse = new HashMap<>();
                    mapResponse.put("errorCode", 600);
                    mapResponse.put("errorMessage", "没有提供token参数");
                    mapResponse.put("data", null);
                    byte[] bytes = OBJECT_MAPPER.writeValueAsBytes(mapResponse);
                    DataBuffer dataBuffer = exchange.getResponse().bufferFactory().wrap(bytes);

                    monoSink.success(dataBuffer);
                } catch (Exception ex) {
                    log.error(ex.getMessage(), ex);
                    monoSink.error(ex);
                }
            }));
        }
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
