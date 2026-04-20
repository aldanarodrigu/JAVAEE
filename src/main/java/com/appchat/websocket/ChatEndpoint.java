package com.appchat.websocket;

import com.appchat.security.JwtUtil;
import jakarta.websocket.*;
import jakarta.websocket.server.HandshakeRequest;
import jakarta.websocket.server.ServerEndpoint;
import jakarta.websocket.server.ServerEndpointConfig;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint(value = "/chat", configurator = ChatEndpoint.Config.class)
public class ChatEndpoint {

    private static Map<Long, Session> sesiones = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session, EndpointConfig config) {

        Object uidObj = config.getUserProperties().get("userId");

        System.out.println("USER PROPERTY RAW: " + uidObj);

        if (uidObj == null) {
            cerrar(session);
            return;
        }

        Long userId = (Long) uidObj;

        sesiones.put(userId, session);
        session.getUserProperties().put("userId", userId);

        System.out.println("Conectado OK: " + userId);
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        Long from = (Long) session.getUserProperties().get("userId");

        // FORMATO: "to:mensaje"
        String[] partes = message.split(":", 2);
        Long to = Long.parseLong(partes[0]);
        String contenido = partes[1];

        Session destino = sesiones.get(to);

        if (destino != null && destino.isOpen()) {
            try {
                destino.getBasicRemote().sendText(from + ":" + contenido);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @OnClose
    public void onClose(Session session) {
        Long userId = (Long) session.getUserProperties().get("userId");
        if (userId != null) {
            sesiones.remove(userId);
        }
    }

    private void cerrar(Session session) {
        try {
            session.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static class Config extends ServerEndpointConfig.Configurator {

    @Override
    public void modifyHandshake(ServerEndpointConfig config,
                                HandshakeRequest request,
                                HandshakeResponse response) {

        String query = request.getQueryString();

        if (query != null) {
            for (String param : query.split("&")) {
                if (param.startsWith("token=")) {

                    String token = param.substring(6);
                    Long userId = JwtUtil.getUserIdFromToken(token);

                    if (userId != null) {
                        config.getUserProperties().put("userId", userId);
                    }
                }
            }
        }
    }
}
}