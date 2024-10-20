package com.future.demo;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import java.net.URLDecoder;
import java.util.Map;

public class WebsocketInterceptor extends HttpSessionHandshakeInterceptor {
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        if(request instanceof ServletServerHttpRequest) {
            ServletServerHttpRequest servletServerHttpRequest = (ServletServerHttpRequest) request;
            String clientId = servletServerHttpRequest.getServletRequest().getParameter("clientId");
            if(!StringUtils.isEmpty(clientId)) {
                clientId = URLDecoder.decode(clientId, "utf-8");
                attributes.put("clientId", clientId);
            }
        }
        return super.beforeHandshake(request, response, wsHandler, attributes);
    }
}
