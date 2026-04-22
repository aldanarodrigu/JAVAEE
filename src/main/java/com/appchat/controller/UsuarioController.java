package com.appchat.controller;

import com.appchat.dto.UsuarioDTO;
import com.appchat.mapper.UsuarioMapper;
import com.appchat.dto.CambiarEstadoDTO;
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
import jakarta.ws.rs.core.Response;

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
        return usuarioRepository.listarActivos()
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

    @PUT
    @Path("/{id}/estado")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public UsuarioDTO cambiarEstado(@PathParam("id") Long id, CambiarEstadoDTO datos) {
        var usuarioActualizado = usuarioRepository.actualizarEstado(id, datos.getEstado());

        if (usuarioActualizado == null) {
            // TODO: devolver HTTP 404 cuando el usuario no exista
            return null;
        }

        return UsuarioMapper.toDTO(usuarioActualizado);
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response obtenerUsuarioPorId(@PathParam("id") Long id) {

        var usuario = usuarioRepository.buscarPorId(id);

        if (usuario == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Usuario no encontrado")
                    .build();
        }

        return Response.ok(UsuarioMapper.toDTO(usuario)).build();
    }


}