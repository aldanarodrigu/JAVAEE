package com.chat.controller;

import com.chat.model.ChatDirect;
import com.chat.model.ChatGroup;
import com.chat.service.ChatService;
import com.chat.util.JwtUtil;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.Map;

@Path("/chats")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ChatController {

    @Inject
    private ChatService chatService;

    @Inject
    private JwtUtil jwtUtil;

    @POST
    @Path("/direct/{targetUserId}")
    public Response getOrCreateDirectChat(
            @HeaderParam(HttpHeaders.AUTHORIZATION) String authHeader,
            @PathParam("targetUserId") Long targetUserId) {
        Long currentUserId = getUserIdFromToken(authHeader);
        ChatDirect chat = chatService.getOrCreateDirectChat(currentUserId, targetUserId);
        return Response.ok(Map.of("chatId", chat.getId())).build();
    }

    @GET
    @Path("/direct")
    public Response getMyDirectChats(@HeaderParam(HttpHeaders.AUTHORIZATION) String authHeader) {
        Long userId = getUserIdFromToken(authHeader);
        List<ChatDirect> chats = chatService.getDirectChatsByUser(userId);
        return Response.ok(chats.stream()
            .map(c -> Map.of(
                "chatId", c.getId(),
                "user1Id", c.getUser1().getId(),
                "user2Id", c.getUser2().getId()
            ))
            .toList()).build();
    }

    @POST
    @Path("/group")
    public Response createGroupChat(
            @HeaderParam(HttpHeaders.AUTHORIZATION) String authHeader,
            Map<String, Object> payload) {
        if (payload == null || payload.get("name") == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(Map.of("error", "name is required")).build();
        }
        Long ownerId = getUserIdFromToken(authHeader);
        String name = payload.get("name").toString();
        if (name.isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(Map.of("error", "name must not be blank")).build();
        }
        String description = payload.getOrDefault("description", "").toString();

        @SuppressWarnings("unchecked")
        List<Integer> memberIdInts = (List<Integer>) payload.getOrDefault("memberIds", List.of());
        List<Long> memberIds = memberIdInts.stream().map(Integer::longValue).toList();

        try {
            ChatGroup chatGroup = chatService.createGroupChat(name, description, ownerId, memberIds);
            return Response.status(Response.Status.CREATED)
                .entity(Map.of("chatId", chatGroup.getId(), "groupId", chatGroup.getGroup().getId()))
                .build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(Map.of("error", e.getMessage())).build();
        }
    }

    @GET
    @Path("/group/{groupId}")
    public Response getGroupChat(@PathParam("groupId") Long groupId) {
        return chatService.getGroupChat(groupId)
            .map(cg -> Response.ok(Map.of(
                "chatId", cg.getId(),
                "groupId", cg.getGroup().getId(),
                "groupName", cg.getGroup().getName()
            )).build())
            .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    private Long getUserIdFromToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new NotAuthorizedException("Missing token");
        }
        return jwtUtil.getUserId(authHeader.substring("Bearer ".length()));
    }
}
