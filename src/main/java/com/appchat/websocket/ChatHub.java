package com.appchat.websocket;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.websocket.Session;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class ChatHub {

    private final Set<Session> sessions = ConcurrentHashMap.newKeySet();
}
