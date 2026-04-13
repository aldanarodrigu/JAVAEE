package com.appchat.service;

import com.appchat.dto.UsuarioDTO;
import com.appchat.model.Usuario;
import com.appchat.model.enums.EstadoUsuario;
import com.appchat.model.enums.RolSistema;
import com.appchat.repository.UsuarioRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped // bean manejado por CDI, basicamente dice que esta clase solo se va a instancias una sola vez
public class UsuarioService{

    @Inject // inyeccion de dependencias, el contenedor automaticamente te da una instancia de UsuarioService (para no hacer UsuarioRepository repository = new UsuarioRepository())
    private UsuarioRepository repository;

    @Transactional // este método corre dentro de una transacción de base de datos, basicamente para que si algo falla aca haga rollback
    public Usuario crearUsuario(UsuarioDTO usuarioDto) {
        
        if (repository.buscarPorEmail(usuarioDto.getEmail()) != null) {
            throw new IllegalArgumentException("El email ya está registrado");
        }
        
        Usuario usuario = new Usuario();
        usuario.setNombre(usuarioDto.getNombre());
        usuario.setApellido(usuarioDto.getApellido());
        usuario.setEmail(usuarioDto.getEmail());
        // TODO: hashear la password antes de guardar
        usuario.setPassword(usuarioDto.getPassword());
        usuario.setEstado(EstadoUsuario.INVISIBLE);
        usuario.setRolSistema(RolSistema.EMPLEADO);
        
        repository.guardar(usuario); // LLAMAS AL REPOSITORIO DE USUARIO, ACA NO PODES DIRECTAMENTE CON LA BD!!
        
        return usuario;
    }
}
