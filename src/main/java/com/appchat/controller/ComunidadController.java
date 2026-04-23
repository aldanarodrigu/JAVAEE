package com.appchat.controller;

import com.appchat.dto.ComunidadDTO;
import com.appchat.dto.InvitacionDTO;
import com.appchat.model.Comunidad;
import com.appchat.service.ComunidadService;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/comunidades")
@Produces(MediaType.APPLICATION_JSON) 
@Consumes(MediaType.APPLICATION_JSON) 
public class ComunidadController {
    
    @Inject
    private ComunidadService comunidadService;
    
    @Context
    private ContainerRequestContext requestContext;
    
    @POST
    public Response crearComunidad(ComunidadDTO dto) {

        Long userId = (Long) requestContext.getProperty("userId"); //trae el usuario logueado
        System.out.println("USER ID: " + userId);

        Comunidad comunidad = comunidadService.crearComunidad(dto, userId);

        return Response.ok().build();
    }
    
    @POST
    @Path("/{id}/invitar")
    public Response invitarUsuario(@PathParam("id") Long comunidadId, InvitacionDTO invitacion){
        
        Long ownerId = (Long) requestContext.getProperty("userId");
        
        comunidadService.invitarUsuario(comunidadId, invitacion.getUsername(), ownerId);
        
        return Response.ok("Invitacion Enviada").build();
    }
    
}
