package mirea.edu.autosys.service;

import java.io.IOException;
import java.util.concurrent.CopyOnWriteArraySet;

import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

@Service
public class WebSocketService extends TextWebSocketHandler {

    private final CopyOnWriteArraySet<WebSocketSession> sessions = new CopyOnWriteArraySet<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sessions.add(session);
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) {
        // Обработка сообщений от клиента, если нужно
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, org.springframework.web.socket.CloseStatus status) {
        sessions.remove(session);
    }

    public void sendMessage(NodeId nodeId, String value) {
        String message = String.format("{\"nodeId\": \"%s\", \"value\": \"%s\"}", nodeId, value);
        for (WebSocketSession session : sessions) {
            try {
                session.sendMessage(new TextMessage(message));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
}