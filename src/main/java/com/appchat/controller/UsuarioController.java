package com.appchat.controller;

import com.appchat.dto.UsuarioDTO;
import com.appchat.dto.UsuarioResponseDTO;
import com.appchat.model.Usuario;
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

    @Inject // Te instancia solo el usuarioService
    private UsuarioService service;

    @POST
    public Response crearUsuario(UsuarioDTO dto) { //recibe un dto porque JAX-RS recibe un JSON y lo convierte a dto
        try {
            Usuario  nuevo = service.crearUsuario(dto); //llamas al service, no podes crear nada desde aca 
            return Response.status(Response.Status.CREATED)
                           .entity("{\"id\": " + nuevo.getId() + "}")
                           .build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.CONFLICT)
                           .entity("{\"error\": \"" + e.getMessage() + "\"}")
                           .build();
        }
    }

    @GET
    public Response listarUsuarios() {
        List<UsuarioResponseDTO> usuarios = service.listarUsuarios();
        return Response.ok(usuarios).build();
    }

    @GET
    @Path("/{id}")
    public Response obtenerUsuario(@PathParam("id") Long id) {
        UsuarioResponseDTO usuario = service.obtenerUsuarioPorId(id);
        if (usuario == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\": \"Usuario no encontrado\"}")
                    .build();
        }

        return Response.ok(usuario).build();
    }
}
