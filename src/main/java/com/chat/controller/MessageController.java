package com.chat.controller;

import com.chat.dto.MessageDTO;
import com.chat.service.MessageService;
import com.chat.util.JwtUtil;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.Map;

@Path("/messages")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class MessageController {

    @Inject
    private MessageService messageService;

    @Inject
    private JwtUtil jwtUtil;

    @POST
    public Response sendMessage(
            @HeaderParam(HttpHeaders.AUTHORIZATION) String authHeader,
            Map<String, Object> payload) {
        if (payload == null || payload.get("chatId") == null || payload.get("content") == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(Map.of("error", "chatId and content are required")).build();
        }
        Long senderId = getSenderIdFromToken(authHeader);
        Long chatId;
        try {
            chatId = Long.parseLong(payload.get("chatId").toString());
        } catch (NumberFormatException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(Map.of("error", "Invalid chatId")).build();
        }
        String content = payload.get("content").toString();
        if (content.isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(Map.of("error", "content must not be blank")).build();
        }

        try {
            MessageDTO dto = messageService.sendMessage(senderId, chatId, content);
            return Response.status(Response.Status.CREATED).entity(dto).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(Map.of("error", e.getMessage())).build();
        }
    }

    @GET
    @Path("/chat/{chatId}")
    public Response getMessagesByChat(@PathParam("chatId") Long chatId) {
        List<MessageDTO> messages = messageService.getMessagesByChat(chatId);
        return Response.ok(messages).build();
    }

    @GET
    @Path("/sender/{senderId}")
    public Response getMessagesBySender(@PathParam("senderId") Long senderId) {
        List<MessageDTO> messages = messageService.getMessagesBySender(senderId);
        return Response.ok(messages).build();
    }

    private Long getSenderIdFromToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new NotAuthorizedException("Missing token");
        }
        return jwtUtil.getUserId(authHeader.substring("Bearer ".length()));
    }
}
