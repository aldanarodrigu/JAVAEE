package com.appchat.websocket;

import com.appchat.security.JwtUtil;
import com.appchat.service.ChatService;
import jakarta.inject.Inject;
import jakarta.websocket.*;
import jakarta.websocket.server.HandshakeRequest;
import jakarta.websocket.server.ServerEndpoint;
import jakarta.websocket.server.ServerEndpointConfig;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

@ServerEndpoint(value = "/chat", configurator = ChatEndpoint.Config.class)
public class ChatEndpoint {

    private static final Logger log = Logger.getLogger(ChatEndpoint.class.getName());

    @Inject
    private ChatHub chatHub;

    @Inject
    private ChatService chatService;

    public static class Config extends ServerEndpointConfig.Configurator {

        @Override
        public void modifyHandshake(ServerEndpointConfig config,
                                    HandshakeRequest request,
                                    HandshakeResponse response) {
            try {
                String query = request.getQueryString();

                if (query == null) return;

                String token = null;

                for (String param : query.split("&")) {
                    if (param.startsWith("token=")) {
                        token = param.substring("token=".length());
                        break;
                    }
                }

                if (token != null) {
                    Long userId = JwtUtil.getUserIdFromToken(token);
                    config.getUserProperties().put("userId", userId);
                }

            } catch (Exception e) {
                Logger.getLogger(Config.class.getName())
                        .warning("Token inválido en handshake: " + e.getMessage());
            }
        }
    }


    @OnOpen
    public void onOpen(Session session, EndpointConfig config) {

        Long userId = (Long) config.getUserProperties().get("userId");

        if (userId == null) {
            try {
                session.close(new CloseReason(
                        CloseReason.CloseCodes.VIOLATED_POLICY,
                        "No autorizado"
                ));
            } catch (IOException e) {
                log.log(Level.WARNING, "Error cerrando sesión no autorizada", e);
            }
            return;
        }

        session.getUserProperties().put("userId", userId);
        
        chatHub.registrar(userId, session);
        log.info("Usuario " + userId + " conectado. Sesiones activas: "
                + chatHub.cantidadSesiones());
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        
        Long userId = (Long) session.getUserProperties().get("userId");
        
        try {
            chatService.procesarMensajeWebSocket(userId, message);

        } catch (IllegalArgumentException e) {
            enviarError(session, "INVALID_MESSAGE", e.getMessage());

        } catch (jakarta.ws.rs.WebApplicationException e) {
            enviarError(session, "ERROR", "Chat no encontrado o sin permiso");

        } catch (Exception e) {
            log.log(Level.SEVERE,
                    "Error procesando mensaje de usuario " + userId, e);

            enviarError(session, "SERVER_ERROR", "Error interno");
        }
    }

    @OnClose
    public void onClose(Session session, CloseReason reason) {
        
        Long userId = (Long) session.getUserProperties().get("userId");
        
        if (userId != null) {
            chatHub.remover(userId, session);
            log.info("Usuario " + userId + " desconectado: "
                    + reason.getReasonPhrase());
        }
    }

    @OnError
    public void onError(Session session, Throwable error) {
        
        Long userId = (Long) session.getUserProperties().get("userId");
        
        log.log(Level.SEVERE,
                "WebSocket error para usuario " + userId, error);
    }

    private void enviarError(Session session, String code, String detail) {
        try {
            String json = String.format(
                    "{\"type\":\"ERROR\",\"code\":\"%s\",\"detail\":\"%s\"}",
                    code, detail
            );
            session.getBasicRemote().sendText(json);
        } catch (IOException e) {
            log.log(Level.WARNING,
                    "No se pudo enviar error al cliente", e);
        }
    }
}