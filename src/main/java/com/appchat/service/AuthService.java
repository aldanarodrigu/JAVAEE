package com.appchat.service;

import com.appchat.model.Usuario;
import com.appchat.dto.UsuarioDTO;
import com.appchat.repository.UsuarioRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.NotFoundException;

import org.mindrot.jbcrypt.BCrypt;

@ApplicationScoped
public class AuthService {
    
    @Inject
    private UsuarioRepository repository;
    
    @Inject
    private UsuarioService usuarioService;

    public Usuario login(String email, String password) {
        Usuario u = repository.buscarPorEmail(email);

        if (u == null) {
            throw new NotFoundException("Usuario no existe");
        }

        if (!BCrypt.checkpw(password, u.getPassword())) {
            throw new ForbiddenException("Password incorrecta");
        }
        
        return u;
    }
    
    public Usuario registrarUsuario(String email, UsuarioDTO dto) {
        Usuario solicitante = repository.buscarPorEmail(email);

        if (solicitante != null) {
            throw new BadRequestException("Email ya registrado.");
        }

        return usuarioService.crearUsuario(dto);
    }
}