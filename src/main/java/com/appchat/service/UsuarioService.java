package com.appchat.service;

import com.appchat.dto.UsuarioDTO;
import com.appchat.model.Usuario;
import com.appchat.repository.UsuarioRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class UsuarioService {

    @Inject
    UsuarioRepository usuarioRepository;

    public UsuarioDTO buscarPorId(Long id) {
        Usuario usuario = usuarioRepository.buscarPorId(id);

        if (usuario == null) {
            return null;
        }

        return mapToDTO(usuario);
    }

    private UsuarioDTO mapToDTO(Usuario usuario) {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setId(usuario.getId());
        dto.setNombre(usuario.getNombre());
        dto.setApellido(usuario.getApellido());
        dto.setEmail(usuario.getEmail());
        dto.setFotoPerfil(usuario.getFotoPerfil());
        dto.setEstado(usuario.getEstado());
        dto.setActivo(usuario.getActivo());
        return dto;
    }
}