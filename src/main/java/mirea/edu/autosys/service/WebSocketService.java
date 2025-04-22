package mirea.edu.autosys.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

@Service
public class WebSocketService extends TextWebSocketHandler {

    private final Set<WebSocketSession> sessions = new CopyOnWriteArraySet<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sessions.add(session);
        System.out.println("New WebSocket connection: " + session.getId());
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) {
        // Обработка сообщений от клиента, если нужно
        System.out.println("Received message: " + message.getPayload());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, org.springframework.web.socket.CloseStatus status) {
        sessions.remove(session);
        System.out.println("WebSocket connection closed: " + session.getId());
    }

    /**
     * Отправляет данные на все активные WebSocket-сессии.
     *
     * @param nodeId Идентификатор узла OPC UA.
     * @param value  Значение узла.
     */
    public void sendMessage(String endpointUrl, NodeId nodeId, String value) {
        Map<String, Object> jsonMessage = new HashMap<>();
        jsonMessage.put("endpointUrl", endpointUrl);
        jsonMessage.put("nodeId", nodeId.toParseableString());
        jsonMessage.put("value", value);
        jsonMessage.put("messageType", "telemetry");


        String jsonString;
        try {
            jsonString = new ObjectMapper().writeValueAsString(jsonMessage);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize JSON", e);
        }

        TextMessage message = new TextMessage(jsonString);
        for (WebSocketSession session : sessions) {
            try {
                if (session.isOpen()) {
                    session.sendMessage(message);
                }
            } catch (IOException e) {
                System.err.println("Failed to send WebSocket message: " + e.getMessage());
            }
        }
    }
}