package com.appchat.controller;

import com.appchat.dto.MensajeDTO;
import com.appchat.service.ChatService;
import jakarta.inject.Inject;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint("/ws/chat/{chatId}")  ///ws/chat/{chatId}?token=JWT
public class ChatWebSocket {

    @Inject
    private ChatService chatService;
    
    /*
    @Inject
    private JwtService jwtService;
    */

    // chatId → (usuarioId → Session)
    // guarda qué sesiones están en cada chat
    private static Map<Long, Map<Long, Session>> sesionesChat = new ConcurrentHashMap<>();

    @OnOpen 
    public void onOpen(Session session, @PathParam("chatId") Long chatId) {
        // extraer token 
        // obtener usuario desde jwt
        // validar que pertenece al chat
        // guardar sesion
        // guardar usuario en propiedades de sesion 
    }

    @OnMessage //cuando se envia mensaje
    public void onMessage(String contenido, Session sesion){
        //recuperar datos de sesion 
        //revalidad que el usuario participe del chat 
    }

    @OnClose
    public void onClose(Session sesion){
        //limpiar sesion
    }

    @OnError
    public void onError(Session sesion, Throwable error) {
        //limpiar sesion
    }

    // manda el mensaje a todos los conectados en ese chat
    private void broadcast(Long chatId, MensajeDTO dto) {
 
    }
    
    /*
    private String extraerToken(Session sesion){
        //jwt
    }
    
    private void limpiarSesion(Session sesion){
    }
    
    private void cerrarSesion(Session sesion){
    }
    
    */
}