package com.appchat.controller;

import com.appchat.model.Usuario;
import com.appchat.repository.UsuarioRepository;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.util.List;

@Path("/usuarios")
public class UsuarioController {

    @Inject
    private UsuarioRepository usuarioRepository;

    @GET
    @Path("/hola")
    @Produces(MediaType.TEXT_PLAIN)
    public String hola() {
        return "Hola gente";
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Usuario> listarUsuarios() {
        return usuarioRepository.findAll();
    }
}