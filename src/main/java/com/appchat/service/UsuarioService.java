package com.appchat.service;

import com.appchat.dto.UsuarioDTO;
import com.appchat.model.Usuario;
import com.appchat.model.enums.RolSistema;
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

    public Usuario crearUsuario(UsuarioDTO dto) {
        if (dto == null) {
            return null;
        }

        Usuario usuario = new Usuario();
        usuario.setNombre(dto.getNombre());
        usuario.setApellido(dto.getApellido());
        usuario.setEmail(dto.getEmail());
        usuario.setFotoPerfil(dto.getFotoPerfil());
        usuario.setEstado(dto.getEstado());
        usuario.setActivo(dto.getActivo() != null ? dto.getActivo() : true);
        usuario.setRolSistema(RolSistema.EMPLEADO);

        usuario.setPasswordHash("1234");

        return usuarioRepository.guardar(usuario);
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