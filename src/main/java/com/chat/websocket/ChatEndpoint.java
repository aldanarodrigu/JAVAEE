package com.chat.websocket;

import com.chat.dto.MessageDTO;
import com.chat.service.MessageService;
import com.chat.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.inject.Inject;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;

import java.io.IOException;
import java.util.Map;

/**
 * WebSocket endpoint for real-time chat.
 * Connect via: ws://host/chat-app/ws/chat/{chatId}?token=<JWT>
 */
@ServerEndpoint("/ws/chat/{chatId}")
public class ChatEndpoint {

    @Inject
    private ChatHub chatHub;

    @Inject
    private MessageService messageService;

    @Inject
    private JwtUtil jwtUtil;

    private final ObjectMapper objectMapper = new ObjectMapper()
        .registerModule(new JavaTimeModule())
        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    @OnOpen
    public void onOpen(Session session, @PathParam("chatId") String chatIdParam) {
        String token = session.getRequestParameterMap()
            .getOrDefault("token", java.util.List.of())
            .stream().findFirst().orElse(null);

        if (token == null || !jwtUtil.isValid(token)) {
            try {
                session.close(new CloseReason(CloseReason.CloseCodes.VIOLATED_POLICY, "Unauthorized"));
            } catch (IOException e) {
                // ignore
            }
            return;
        }

        Long chatId;
        try {
            chatId = Long.parseLong(chatIdParam);
        } catch (NumberFormatException e) {
            try {
                session.close(new CloseReason(CloseReason.CloseCodes.UNEXPECTED_CONDITION, "Invalid chatId"));
            } catch (IOException ignored) {
                // ignore
            }
            return;
        }
        session.getUserProperties().put("chatId", chatId);
        session.getUserProperties().put("userId", jwtUtil.getUserId(token));
        session.getUserProperties().put("username", jwtUtil.getUsername(token));

        chatHub.join(chatId, session);
    }

    @OnMessage
    public void onMessage(Session session, String messageText) {
        Long chatId = (Long) session.getUserProperties().get("chatId");
        Long userId = (Long) session.getUserProperties().get("userId");

        if (chatId == null || userId == null) {
            return;
        }

        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> payload = objectMapper.readValue(messageText, Map.class);
            String content = payload.getOrDefault("content", "").toString();

            if (content.isBlank()) {
                return;
            }

            MessageDTO saved = messageService.sendMessage(userId, chatId, content);
            String outbound = objectMapper.writeValueAsString(saved);
            chatHub.broadcast(chatId, outbound);
        } catch (Exception e) {
            try {
                session.getBasicRemote().sendText("{\"error\":\"Failed to process message\"}");
            } catch (IOException ignored) {
                // ignore
            }
        }
    }

    @OnClose
    public void onClose(Session session, @PathParam("chatId") String chatIdParam) {
        Long chatId = (Long) session.getUserProperties().get("chatId");
        if (chatId != null) {
            chatHub.leave(chatId, session);
        }
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        Long chatId = (Long) session.getUserProperties().get("chatId");
        if (chatId != null) {
            chatHub.leave(chatId, session);
        }
    }
}
