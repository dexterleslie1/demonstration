package com.future.demo.gateway.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

/**
 * Spring Cloud Gateway 全局通用异常处理
 * https://www.cnblogs.com/leng-leng/p/13064060.html
 */
@Slf4j
@Order(-1)
@RequiredArgsConstructor
@Configuration
public class GlobalExceptionConfiguration implements ErrorWebExceptionHandler {
    final static ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        if(ex!=null) {
            log.error(ex.getMessage(), ex);
        }
        ServerHttpResponse response = exchange.getResponse();

        if (response.isCommitted()) {
            return Mono.error(ex);
        }

        // header set
        String errorMessage;
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        if (ex instanceof ResponseStatusException) {
            HttpStatus httpStatus = ((ResponseStatusException) ex).getStatus();
            if(httpStatus==HttpStatus.SERVICE_UNAVAILABLE) {
                errorMessage = "服务正在启动中，稍后重试";
            } else if(httpStatus==HttpStatus.NOT_FOUND) {
                String uri = exchange.getRequest().getURI().getPath();
                errorMessage = "访问资源 " + uri + " 不存在";
            } else {
                errorMessage = "网络繁忙，稍后重试";
            }
            response.setStatusCode(httpStatus);
        } else {
            errorMessage = "网络繁忙，稍后重试";
        }

        String finalErrorMessage = errorMessage;
        return response
                .writeWith(Mono.fromSupplier(() -> {
                    DataBufferFactory bufferFactory = response.bufferFactory();
                    try {
                        Map<String, Object> responseMapper = new HashMap<>();
                        responseMapper.put("errorCode", 600);
                        responseMapper.put("errorMessage", finalErrorMessage);
                        return bufferFactory.wrap(OBJECT_MAPPER.writeValueAsBytes(responseMapper));
                    } catch (JsonProcessingException e) {
                        log.warn(e.getMessage(), e);
                        return bufferFactory.wrap(new byte[0]);
                    }
                }));
    }
}