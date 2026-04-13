package com.appchat.mapper;

import com.appchat.dto.UsuarioDTO;
import com.appchat.model.Usuario;

public class UsuarioMapper {

    public static UsuarioDTO toDTO(Usuario usuario) {
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