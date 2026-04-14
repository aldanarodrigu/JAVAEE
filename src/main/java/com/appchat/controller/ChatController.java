package com.appchat.controller;

import com.appchat.dto.ChatDirectoRequestDTO;
import com.appchat.dto.ChatResumenDTO;
import com.appchat.dto.HistorialMensajesDTO;
import com.appchat.model.Usuario;
import com.appchat.service.ChatService;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import java.security.Principal;
import java.util.List;

@Path("/chats")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ChatController {

    @Inject
    private ChatService service;

    @Context
    private SecurityContext securityContext;

    @GET
    public Response listarChats() {
        Long usuarioId = obtenerUsuarioAutenticado().getId();
        List<ChatResumenDTO> chats = service.listarChatsDelUsuario(usuarioId);
        return Response.ok(chats).build();
    }

    @GET
    @Path("/{id}/mensajes")
    public Response historialMensajes(@PathParam("id") Long chatId,
                                      @QueryParam("page") @DefaultValue("0") int page,
                                      @QueryParam("size") @DefaultValue("20") int size) {
        Long usuarioId = obtenerUsuarioAutenticado().getId();
        HistorialMensajesDTO historial = service.obtenerHistorialMensajes(chatId, usuarioId, page, size);
        return Response.ok(historial).build();
    }

    @POST
    public Response crearOAbrirChatDirecto(ChatDirectoRequestDTO request) {
        if (request == null || request.getUsuarioDestinoId() == null) {
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }

        Long usuarioId = obtenerUsuarioAutenticado().getId();
        ChatResumenDTO chat = service.crearOAbrirChatDirecto(usuarioId, request.getUsuarioDestinoId());
        return Response.ok(chat).build();
    }
    

    private Usuario obtenerUsuarioAutenticado() {
        Principal principal = securityContext != null ? securityContext.getUserPrincipal() : null;
        if (principal == null) {
            throw new WebApplicationException(Response.Status.UNAUTHORIZED);
        }

        return service.resolverUsuarioAutenticado(principal.getName());
    }
}