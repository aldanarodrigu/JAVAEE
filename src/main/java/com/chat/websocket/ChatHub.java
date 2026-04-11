package com.chat.websocket;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.websocket.Session;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class ChatHub {

    // chatId -> set of sessions
    private final Map<Long, Set<Session>> chatSessions = new ConcurrentHashMap<>();

    public void join(Long chatId, Session session) {
        chatSessions.computeIfAbsent(chatId, k -> Collections.newSetFromMap(new ConcurrentHashMap<>()))
            .add(session);
    }

    public void leave(Long chatId, Session session) {
        Set<Session> sessions = chatSessions.get(chatId);
        if (sessions != null) {
            sessions.remove(session);
        }
    }

    public void broadcast(Long chatId, String message) {
        Set<Session> sessions = chatSessions.getOrDefault(chatId, Collections.emptySet());
        java.util.List<Session> toRemove = new java.util.ArrayList<>();
        for (Session session : sessions) {
            if (session.isOpen()) {
                try {
                    session.getBasicRemote().sendText(message);
                } catch (IOException e) {
                    toRemove.add(session);
                }
            }
        }
        sessions.removeAll(toRemove);
    }

    public Set<Session> getSessions(Long chatId) {
        return chatSessions.getOrDefault(chatId, Collections.emptySet());
    }
}
