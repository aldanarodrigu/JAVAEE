package com.appchat.service;

import com.appchat.dto.ComunidadDTO;
import com.appchat.model.Comunidad;
import com.appchat.model.Usuario;
import com.appchat.model.enums.RolComunidad;
import com.appchat.repository.ComunidadRepository;

import jakarta.inject.Inject;

import jakarta.transaction.Transactional;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.ClientErrorException;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;

@ApplicationScoped
public class ComunidadService {
    
    @Inject
    private ComunidadRepository comunidadRepository;
    
    @Inject 
    private UsuarioService usuarioService;

    Comunidad buscarPorId(Long comunidadId) {
        return comunidadRepository.buscarPorId(comunidadId);
    }

    boolean sonMiembros(Long comunidadId, Long usuarioAutenticadoId, Long usuarioDestinoId) {
        return comunidadRepository.sonMiembros(comunidadId, usuarioAutenticadoId, usuarioDestinoId);
    }

    @Transactional
    public Comunidad crearComunidad(ComunidadDTO comunidadDto, Long userId) {
        
        Comunidad comunidadNueva = new Comunidad();
        comunidadNueva.setNombre(comunidadDto.getNombre());
        comunidadNueva.setDescripcion(comunidadDto.getDescripcion());
        comunidadNueva.setFotoUrl(comunidadDto.getFotoUrl());
        
        Usuario creador = usuarioService.obtenerPorId(userId);
        comunidadNueva.agregarMiembro(creador, RolComunidad.OWNER);
        
        comunidadRepository.guardar(comunidadNueva);
        
        return comunidadNueva;
    }

    public void invitarUsuario(Long comunidadId, String username, Long ownerId) {
        
        Comunidad comunidad = comunidadRepository.buscarPorId(comunidadId);
        
        if(!comunidad.esAdmin(ownerId)){
            throw new ForbiddenException("No Autorizado");
        }
        
        Usuario u = usuarioService.buscarPorUsername(username);
        
        if(u == null){
            throw new NotFoundException("Usuario no existe.");
        }
        
        if(comunidad.esMiembro(u.getId())){
            throw new ClientErrorException("Ya es miembro", Response.Status.CONFLICT);
        }
           
        //Aca crear una InitacionAComunidad con estado pendiente asi despues el usuario puede aceptar
        
    }
    
}
