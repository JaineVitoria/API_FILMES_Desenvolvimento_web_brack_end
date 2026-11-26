package com.ifsp.apifilmes.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

@Component
public class FilmeWebSocketHandler extends TextWebSocketHandler {

    private final Set<WebSocketSession> sessoes = new CopyOnWriteArraySet<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sessoes.add(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessoes.remove(session);
    }

    public void notificar(String evento, Integer id, String titulo) {
        try {
            Map<String, Object> mensagem = new LinkedHashMap<>();
            mensagem.put("evento", evento);
            mensagem.put("id", id);
            mensagem.put("titulo", titulo);

            TextMessage texto = new TextMessage(objectMapper.writeValueAsString(mensagem));

            for (WebSocketSession sessao : sessoes) {
                if (sessao.isOpen()) {
                    synchronized (sessao) {
                        sessao.sendMessage(texto);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}