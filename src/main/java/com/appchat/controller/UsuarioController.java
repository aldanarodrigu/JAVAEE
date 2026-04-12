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
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Consumes;
import com.appchat.dto.ActualizarUsuarioDTO;

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

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public UsuarioDTO actualizarUsuario(@PathParam("id") Long id, ActualizarUsuarioDTO datos) {

        var usuarioActualizado = usuarioRepository.actualizarUsuario(
                id,
                datos.getNombre(),
                datos.getApellido(),
                datos.getFotoPerfil()
        );

        if (usuarioActualizado == null) {
            // TODO: devolver HTTP 404 (Not Found) en lugar de null
            // cuando el usuario no exista, usando Response o WebApplicationException
            return null;
        }

        return UsuarioMapper.toDTO(usuarioActualizado);
    }


}