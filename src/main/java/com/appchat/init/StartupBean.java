package com.appchat.init;

import com.appchat.model.Usuario;
import com.appchat.model.EstadoUsuario;
import com.appchat.model.enums.RolSistema;
import com.appchat.repository.UsuarioRepository;

import jakarta.annotation.PostConstruct;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import org.mindrot.jbcrypt.BCrypt;

@Startup // El Spuer Admin se crea al levantar el server, siempre y cuando no exista
@Singleton
public class StartupBean {

    @Inject
    private UsuarioRepository repository;

    @PostConstruct
    @Transactional
    public void init() {

        String emailAdmin = "admin@test.com";

        Usuario existente = repository.buscarPorEmail(emailAdmin);

        if (existente != null) {
            System.out.println("SUPER_ADMIN ya existe");
            return;
        }

        Usuario admin = new Usuario();
        admin.setNombre("Admin");
        admin.setApellido("Principal");
        admin.setEmail(emailAdmin);

        String hashed = BCrypt.hashpw("1234", BCrypt.gensalt());
        admin.setPassword(hashed);

        admin.setRolSistema(RolSistema.SUPER_ADMIN);
        admin.setEstado(EstadoUsuario.DESCONECTADO);

        repository.guardar(admin);

        System.out.println("SUPER_ADMIN creado automáticamente");
    }
}