package com.appchat.repository;

import com.appchat.model.Usuario;
import com.appchat.model.EstadoUsuario;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class UsuarioRepository {

    private final List<Usuario> usuarios = new ArrayList<>();

    @PostConstruct
    public void init() {

        Usuario u1 = new Usuario();
        u1.setId(1L);
        u1.setNombre("José");
        u1.setApellido("Artigas");
        u1.setEmail("artigas@uruguay.uy");
        u1.setPasswordHash("1234");
        u1.setEstado(EstadoUsuario.EN_LINEA);

        Usuario u2 = new Usuario();
        u2.setId(2L);
        u2.setNombre("Fructuoso");
        u2.setApellido("Rivera");
        u2.setEmail("rivera@uruguay.uy");
        u2.setPasswordHash("1234");
        u2.setEstado(EstadoUsuario.OCUPADO);

        Usuario u3 = new Usuario();
        u3.setId(3L);
        u3.setNombre("Manuel");
        u3.setApellido("Oribe");
        u3.setEmail("oribe@uruguay.uy");
        u3.setPasswordHash("1234");
        u3.setEstado(EstadoUsuario.AUSENTE);

        usuarios.add(u1);
        usuarios.add(u2);
        usuarios.add(u3);
    }

    public List<Usuario> findAll() {
        return usuarios;
    }
}