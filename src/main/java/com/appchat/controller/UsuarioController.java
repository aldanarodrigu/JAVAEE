package com.appchat.controller;

import com.appchat.dto.UsuarioDTO;
import com.appchat.dto.UsuarioResponseDTO;
import com.appchat.model.Usuario;
import com.appchat.dto.ActualizarUsuarioDTO;
import com.appchat.service.UsuarioService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/usuarios")
@Produces(MediaType.APPLICATION_JSON) // todo lo que devuelva este controller es JSON 
@Consumes(MediaType.APPLICATION_JSON) // y recibe JSON tambien
public class UsuarioController {

    @Inject
    private UsuarioService service;

    @POST
    public Response crearUsuario(UsuarioDTO dto) {
        try {
            Usuario nuevo = service.crearUsuario(dto);

            UsuarioResponseDTO response = new UsuarioResponseDTO();
            response.setId(nuevo.getId());

            return Response.status(Response.Status.CREATED).entity(response).build();

        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.CONFLICT).entity(e.getMessage()).build();
        }
    }

    @GET
    public Response listarUsuarios() {
        List<UsuarioResponseDTO> usuarios = service.listarUsuarios();
        return Response.ok(usuarios).build();
    }

    @GET
    @Path("/buscar")
    public Response buscarUsuarios(@QueryParam("q") String q) {
        if (q == null || q.trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Debe indicar un texto de búsqueda")
                    .build();
        }

        List<UsuarioResponseDTO> usuarios = service.buscarUsuarios(q);
        return Response.ok(usuarios).build();
    }

    @GET
    @Path("/{id}")
    public Response obtenerUsuario(@PathParam("id") Long id) {
        UsuarioResponseDTO usuario = service.obtenerUsuarioPorId(id);

        if (usuario == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Usuario no encontrado").build();
        }

        return Response.ok(usuario).build();
    }

    @PUT
    @Path("/{id}")
    public Response actualizarUsuario(@PathParam("id") Long id, ActualizarUsuarioDTO dto) {
        UsuarioResponseDTO usuarioActualizado = service.actualizarUsuario(id, dto);

        if (usuarioActualizado == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Usuario no encontrado")
                    .build();
        }

        return Response.ok(usuarioActualizado).build();
    }
}
