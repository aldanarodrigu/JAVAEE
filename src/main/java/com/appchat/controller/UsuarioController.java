package com.appchat.controller;

import com.appchat.dto.UsuarioDTO;
import com.appchat.mapper.UsuarioMapper;
import jakarta.ws.rs.QueryParam;
import com.appchat.repository.UsuarioRepository;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.util.List;
import java.util.stream.Collectors;

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
    public List<UsuarioDTO> listarUsuarios() {
        return usuarioRepository.findAll()
                .stream()
                .map(UsuarioMapper::toDTO)
                .collect(Collectors.toList());
    }

    @GET
    @Path("/buscar")
    @Produces(MediaType.APPLICATION_JSON)
    public List<UsuarioDTO> buscarUsuarios(@QueryParam("q") String q) {
        return usuarioRepository.buscarPorNombreOEmail(q)
                .stream()
                .map(UsuarioMapper::toDTO)
                .collect(Collectors.toList());
    }


}