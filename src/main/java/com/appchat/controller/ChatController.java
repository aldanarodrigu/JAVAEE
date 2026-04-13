package com.appchat.controller;

import com.appchat.dto.ChatDirectoRequestDTO;
import com.appchat.dto.ChatDirectoResponseDTO;
import com.appchat.service.ChatService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

@Path("/chats")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ChatController {

    @Inject
    private ChatService service;

    @Context
    private SecurityContext securityContext;

    @GET
    public Response listarChatsDelAutenticado() {
        try {
            Long usuarioId = getUsuarioAutenticadoId();
            return Response.ok(service.listarChats(usuarioId)).build();
        } catch (NotAuthorizedException e) {
            return Response.status(Response.Status.UNAUTHORIZED).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/{id}/mensajes")
    public Response historialMensajes(
        @PathParam("id") Long chatId,
        @DefaultValue("0") @QueryParam("page") int page,
        @DefaultValue("20") @QueryParam("size") int size
    ) {
        try {
            Long usuarioId = getUsuarioAutenticadoId();
            return Response.ok(service.historial(chatId, usuarioId, page, size)).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (NotAuthorizedException e) {
            return Response.status(Response.Status.UNAUTHORIZED).entity(e.getMessage()).build();
        } catch (ForbiddenException e) {
            return Response.status(Response.Status.FORBIDDEN).entity(e.getMessage()).build();
        }
    }

    @POST
    public Response crearOAbrirChatDirecto(ChatDirectoRequestDTO request) {
        try {
            Long usuarioId = getUsuarioAutenticadoId();
            ChatDirectoResponseDTO response = service.crearOAbrirDirecto(usuarioId, request);

            Response.Status status = response.isCreado()
                ? Response.Status.CREATED
                : Response.Status.OK;

            return Response.status(status).entity(response).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (NotAuthorizedException e) {
            return Response.status(Response.Status.UNAUTHORIZED).entity(e.getMessage()).build();
        } catch (NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        }
    }

    private Long getUsuarioAutenticadoId() {
        if (securityContext == null || securityContext.getUserPrincipal() == null) {
            throw new NotAuthorizedException("No autenticado");
        }

        String principalName = securityContext.getUserPrincipal().getName();
        return service.resolverUsuarioIdDesdePrincipal(principalName);
    }
}
