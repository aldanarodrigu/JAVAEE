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

            String token = JwtUtil.generarToken(u.getId(), u.getEmail());

            return Response.ok(token).build();

        }catch (IllegalArgumentException e) {
            return Response.status(Response.Status.UNAUTHORIZED).entity(e.getMessage()).build();
        }
    }
    
    
    @POST
    @Path("/registro")
    public Response registrarUsuario(UsuarioDTO dto) {

        try {
            Usuario nuevo = authService.registrarUsuario(dto.getEmail(), dto);

            return Response.status(Response.Status.CREATED).entity(nuevo.getId()).build();

        } catch (SecurityException e) {
            return Response.status(Response.Status.FORBIDDEN).entity(e.getMessage()).build();

        }catch(Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error interno").build();
        }
    }
}    