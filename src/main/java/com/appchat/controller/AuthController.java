package com.appchat.controller;

import com.appchat.dto.LoginDTO;
import com.appchat.dto.UsuarioDTO;
import com.appchat.model.Usuario;
import com.appchat.security.JwtUtil;
import com.appchat.service.AuthService;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Context;


@Path("/auth")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AuthController {

    @Inject
    private AuthService authService;

    @POST
    @Path("/login")
    public Response login(LoginDTO dto) {
        try {
            Usuario u = authService.login(dto.getEmail(), dto.getPassword());

            String token = JwtUtil.generarToken(u.getId(), u.getEmail(), u.getRolSistema().name());

            return Response.ok("{\"token\": \"" + token + "\"}").build();

        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}")
                    .build();
        }
    }
    
    
    @POST
    @Path("/registro")
    public Response registrarUsuario(
        @Context ContainerRequestContext requestContext,
        UsuarioDTO dto) {

    try {
        String email = (String) requestContext.getProperty("email");

        Usuario nuevo = authService.registrarUsuario(email, dto);
        
        return Response.status(201)
                .entity("{\"id\": " + nuevo.getId() + "}")
                .build();

    } catch (SecurityException e) {
        return Response.status(403)
                .entity("{\"error\": \"" + e.getMessage() + "\"}")
                .build();

    } catch (Exception e) {
        e.printStackTrace();
        return Response.status(500).entity("Error interno").build();
    }
    }    
}