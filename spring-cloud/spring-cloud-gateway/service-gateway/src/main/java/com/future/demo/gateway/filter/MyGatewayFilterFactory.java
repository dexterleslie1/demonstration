package com.future.demo.gateway.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * 局部filter
 */
@Slf4j
@Component
public class MyGatewayFilterFactory extends AbstractGatewayFilterFactory<Object> {

    final static ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public GatewayFilter apply(Object config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            String originalUri = request.getURI().getPath();
            String uri = originalUri.substring(1);
            String[] uriParts = uri.split("/", 2);
            if(uriParts.length<2) {
                ServerHttpResponse response = exchange.getResponse();
                response.setStatusCode(HttpStatus.BAD_REQUEST);
                response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

                final String errMsg = "请求路径 " + originalUri + " 错误，例如: http://localhost:8080/obs-default-epu-555-33/2021-08-20/uuu-01.jpg" ;
                return response.writeWith(Mono.create(monoSink -> {
                    try {
                        Map<String, Object> errorResponseMap = new HashMap<>();
                        errorResponseMap.put("errorCode", 600);
                        errorResponseMap.put("errorMessage", errMsg);
                        String responseJSON = OBJECT_MAPPER.writeValueAsString(errorResponseMap);
                        DataBuffer dataBuffer = response.bufferFactory().wrap(responseJSON.getBytes(StandardCharsets.UTF_8));
                        monoSink.success(dataBuffer);
                    }
                    catch (Exception ex) {
                        log.error("对象输出异常", ex);
                        monoSink.error(ex);
                    }
                }));
            }

            String bucketName = uriParts[0];
            String objectName = uriParts[1];
            String obsPrefix = "obs-";
            bucketName = bucketName.substring(obsPrefix.length());
            try {
                objectName = URLEncoder.encode(objectName, StandardCharsets.UTF_8.name());
            } catch (UnsupportedEncodingException e) {
                log.error(e.getMessage(), e);
            }
            log.debug("bucketName=" + bucketName + ",objectName=" + objectName);

            request = request.mutate()
                    .uri(URI.create("/api/v1/oss/getObject"))
                    .header("bucketName", bucketName)
                    .header("objectName", objectName)
                    .build();
            return chain.filter(exchange.mutate().request(request).build());
        };
    }
}
