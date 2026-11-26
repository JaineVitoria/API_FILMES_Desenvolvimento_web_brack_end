package com.ifsp.apifilmes.websocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final FilmeWebSocketHandler filmeWebSocketHandler;

    public WebSocketConfig(FilmeWebSocketHandler filmeWebSocketHandler) {
        this.filmeWebSocketHandler = filmeWebSocketHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(filmeWebSocketHandler, "/ws/filmes")
                .setAllowedOrigins("*");
    }
}