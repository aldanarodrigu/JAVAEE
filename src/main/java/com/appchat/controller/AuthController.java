package com.appchat.controller;

import com.appchat.dto.LoginDTO;
import com.appchat.dto.UsuarioDTO;
import com.appchat.model.Usuario;
import com.appchat.model.enums.RolSistema;
import com.appchat.repository.UsuarioRepository;
import com.appchat.security.JwtUtil;
import com.appchat.service.AuthService;
import com.appchat.service.UsuarioService;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

@Path("/auth")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AuthController {

    @Inject
    private AuthService authService;
    
    @Inject
    private UsuarioRepository repository;
    
    @Inject
    private UsuarioService service;

    @POST
    @Path("/login")
    public Response login(LoginDTO dto) {
        try {
            Usuario u = authService.login(dto.getEmail(), dto.getPassword());

            String token = JwtUtil.generarToken(u.getEmail());

            return Response.ok("{\"token\": \"" + token + "\"}").build();

        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}")
                    .build();
        }
    }
    
    // -- REGISTRO --
    
    @POST
    @Path("/registro")
    public Response registrarUsuario(
            @HeaderParam("Authorization") String authHeader,
            UsuarioDTO dto) {

        try {
            // Validar header
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return Response.status(401).entity("Falta token").build();
            }

            String token = authHeader.substring("Bearer ".length());

            // Leer token
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(JwtUtil.getKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            String email = claims.getSubject();

            // Buscar usuario
            Usuario solicitante = repository.buscarPorEmail(email);

            if (solicitante == null) {
                return Response.status(401).entity("Usuario no válido").build();
            }

            // Validar rol
            if (solicitante.getRolSistema() != RolSistema.SUPER_ADMIN) {
                return Response.status(403).entity("No autorizado").build();
            }

            // Crear usuario (reutilizás lógica)
            Usuario nuevo = service.crearUsuario(dto);

            return Response.status(201)
                    .entity("{\"id\": " + nuevo.getId() + "}")
                    .build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(500).entity(e.getMessage()).build();
            //return Response.status(500).entity("Error en registro").build();
        }
    }
    
}