package com.future.demo.gateway.config;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.future.demo.common.feign.UserFeignIntranet;
import com.future.demo.common.vo.PermissionVo;
import com.yyd.common.jwt.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Component
@Slf4j
public class SecurityFilter implements GlobalFilter, Ordered {

    @Resource
    UserFeignIntranet userFeignIntranet;

    @Value("${publicKey}")
    String publicKey;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();

        if("/api/v1/auth/loginWithPassword".equals(path)) {
            return chain.filter(exchange);
        }

        String token = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if(StringUtils.isBlank(token)) {
            return responseWithError(exchange, HttpStatus.UNAUTHORIZED,50002, "未登陆");
        }

        try {
            token = token.replace("Bearer ", "");

            DecodedJWT decodedJWT = JwtUtil.verifyWithPublicKey(publicKey, token);
            String userId = String.valueOf(decodedJWT.getClaim("userId").asLong());

            if("/api/v1/auth/refreshToken".contains(path)) {
                // 不能使用非refreshToken请求 /api/v1/auth/refreshToken接口
                String tokenType = decodedJWT.getClaim("type").asString();
                if(!tokenType.equals("refreshToken")) {
                    return responseWithError(exchange, HttpStatus.FORBIDDEN,50000, "不能使用accessToken调用refreshToken接口");
                }
            } else {
                // 不能使用非accessToken请求除 /api/v1/auth/refreshToken 之外的接口
                String tokenType = decodedJWT.getClaim("type").asString();
                if(!tokenType.equals("accessToken")) {
                    return responseWithError(exchange, HttpStatus.FORBIDDEN,50000, "不能使用refreshToken调用业务接口");
                }
            }

            List<String> permissionList = decodedJWT.getClaim("permissionList").asList(String.class);
            List<String> authorities = permissionList == null ? new ArrayList<>() : permissionList;
            List<String> roleList = decodedJWT.getClaim("roleList").asList(String.class);
            if (roleList != null && roleList.size() > 0) {
                authorities.addAll(roleList);
            }

            CompletableFuture<List<PermissionVo>> future = CompletableFuture.supplyAsync(() -> this.userFeignIntranet.listPermission().getData());
            List<PermissionVo> permissionVoList = future.get();
            Map<String, PermissionVo> urlToPermissionVoMapper = permissionVoList.stream().collect(Collectors.toMap(PermissionVo::getUrl, o -> o));
            PermissionVo permissionVo = urlToPermissionVoMapper.get(path);
            if (permissionVo != null) {
                boolean valid = true;

                List<String> permissionListAccessing = permissionVo.getPermissionListAccessing();
                if (permissionListAccessing != null) {
                    for (String o : permissionListAccessing) {
                        if (!authorities.contains(o)) {
                            valid = false;
                            break;
                        }
                    }
                }

                List<String> roleListAccessing = permissionVo.getRoleListAccessing();
                if (roleListAccessing != null) {
                    for (String o : roleListAccessing) {
                        if (!authorities.contains(o)) {
                            valid = false;
                            break;
                        }
                    }
                }

                if (!valid) {
                    return responseWithError(exchange, HttpStatus.FORBIDDEN,50001, "调用接口[uri=" + exchange.getRequest().getPath() + "]权限不足！");
                }
            } else {
                return responseWithError(exchange, HttpStatus.FORBIDDEN,50001, "调用接口[uri=" + exchange.getRequest().getPath() + "]权限不足！");
            }

            ServerHttpRequest request = exchange.getRequest();
            request = request.mutate()
                    .uri(URI.create(path + "?userId=" + userId))
                    .build();
            return chain.filter(exchange.mutate().request(request).build());
        } catch (TokenExpiredException e) {
            return responseWithError(exchange, HttpStatus.BAD_REQUEST,50000, "token已经过期");
        } catch (Exception e) {
            return responseWithError(exchange, HttpStatus.UNAUTHORIZED,50002, "未登陆");
        }
    }

    Mono<Void> responseWithError(ServerWebExchange exchange, HttpStatus httpStatus, int errorCode, String errorMessage) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        response.getHeaders().add("Content-Type", "application/json; charset=UTF-8");
        ObjectNode objectNode = JsonNodeFactory.instance.objectNode();
        objectNode.put("errorCode", errorCode);
        objectNode.put("errorMessage", errorMessage);
        DataBuffer bodyDataBuffer = response.bufferFactory().wrap(objectNode.toString().getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(bodyDataBuffer));
    }

    @Override
    public int getOrder() {
        return 0;
    }

}
