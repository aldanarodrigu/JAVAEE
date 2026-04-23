package com.appchat.service;

import com.appchat.dto.ComunidadDTO;
import com.appchat.model.Comunidad;
import com.appchat.model.Usuario;
import com.appchat.model.enums.RolComunidad;
import com.appchat.repository.ComunidadRepository;
import static com.fasterxml.jackson.databind.cfg.CoercionInputShape.String;
import jakarta.inject.Inject;
import jakarta.resource.spi.ApplicationServerInternalException;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.WebApplicationException;

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
            throw new WebApplicationException("No Autorizado", 403);
        }
        
        Usuario u = usuarioService.buscarPorUsername(username);
        
        if(u == null){
            throw new WebApplicationException("Usuario no existe", 404);
        }
        
        if(comunidad.esMiembro(u.getId())){
            throw new WebApplicationException("Ya es miembro", 409);
        }
           
        //Aca crear una InitacionAComunidad con estado pendiente asi despues el usuario puede aceptar
        
    }
    
}
