package com.appchat.repository;

import com.appchat.model.Usuario;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class UsuarioRepository {

    private final List<Usuario> usuarios = new ArrayList<>();

    @PostConstruct
    public void init() {
        usuarios.add(new Usuario(1L, "pablo@mail.com", "1234", "Pablo", "De Leon"));
        usuarios.add(new Usuario(2L, "ana@mail.com", "1234", "Ana", "Perez"));
        usuarios.add(new Usuario(3L, "luis@mail.com", "1234", "Luis", "Gomez"));
    }

    public List<Usuario> findAll() {
        return usuarios;
    }
}