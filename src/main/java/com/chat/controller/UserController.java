package com.chat.controller;

import com.chat.dto.UserDTO;
import com.chat.service.UserService;
import com.chat.util.JwtUtil;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.Map;

@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserController {

    @Inject
    private UserService userService;

    @Inject
    private JwtUtil jwtUtil;

    @POST
    @Path("/register")
    public Response register(UserDTO dto) {
        try {
            UserDTO created = userService.register(dto);
            return Response.status(Response.Status.CREATED).entity(created).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.CONFLICT)
                .entity(Map.of("error", e.getMessage())).build();
        }
    }

    @POST
    @Path("/login")
    public Response login(UserDTO dto) {
        try {
            Map<String, String> result = userService.login(dto.getUsername(), dto.getPassword());
            return Response.ok(result).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.UNAUTHORIZED)
                .entity(Map.of("error", e.getMessage())).build();
        }
    }

    @GET
    @Path("/{id}")
    public Response getUser(@PathParam("id") Long id) {
        return userService.findById(id)
            .map(u -> Response.ok(u).build())
            .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @GET
    public Response getAllUsers() {
        return Response.ok(userService.findAll()).build();
    }

    @GET
    @Path("/me")
    public Response getCurrentUser(@HeaderParam(HttpHeaders.AUTHORIZATION) String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        String token = authHeader.substring("Bearer ".length());
        String username = jwtUtil.getUsername(token);
        return userService.findByUsername(username)
            .map(u -> Response.ok(u).build())
            .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }
}
