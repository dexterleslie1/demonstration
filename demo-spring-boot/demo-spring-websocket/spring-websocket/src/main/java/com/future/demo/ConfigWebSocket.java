package com.future.demo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class ConfigWebSocket implements WebSocketConfigurer {

    /**
     *
     * @param registry
     */
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(createWebSocketHandler(), "/connector").addInterceptors(websocketInterceptor());
    }

    /**
     *
     * @return
     */
    @Bean
    public WebSocketRequestHandler createWebSocketHandler(){
        WebSocketRequestHandler webSocketHandler = new WebSocketRequestHandler();
        return webSocketHandler;
    }

    @Bean
    WebsocketInterceptor websocketInterceptor() {
        WebsocketInterceptor interceptor = new WebsocketInterceptor();
        return interceptor;
    }
}