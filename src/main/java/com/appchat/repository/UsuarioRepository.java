package com.appchat.repository;

import com.appchat.model.EstadoUsuario;
import com.appchat.model.Usuario;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
@Transactional
public class UsuarioRepository {

    @PersistenceContext
    EntityManager em;

    public List<Usuario> findAll() {
        return em.createQuery("SELECT u FROM Usuario u", Usuario.class)
                .getResultList();
    }

    public Usuario buscarPorId(Long id) {
        return em.find(Usuario.class, id);
    }

    public Usuario buscarPorEmail(String email) {
        List<Usuario> resultados = em.createQuery(
                        "SELECT u FROM Usuario u WHERE LOWER(u.email) = LOWER(:email)", Usuario.class)
                .setParameter("email", email)
                .getResultList();

        return resultados.isEmpty() ? null : resultados.get(0);
    }

    public List<Usuario> buscarPorNombreOEmail(String q) {
        if (q == null || q.isBlank()) {
            return List.of();
        }

        String texto = "%" + q.toLowerCase() + "%";

        return em.createQuery("""
                SELECT u
                FROM Usuario u
                WHERE LOWER(u.nombre) LIKE :texto
                   OR LOWER(u.apellido) LIKE :texto
                   OR LOWER(u.email) LIKE :texto
                """, Usuario.class)
                .setParameter("texto", texto)
                .getResultList();
    }

    public Usuario guardar(Usuario usuario) {
        if (usuario.getId() == null) {
            em.persist(usuario);
            return usuario;
        }
        return em.merge(usuario);
    }

    public Usuario actualizarUsuario(Long id, String nombre, String apellido, String fotoPerfil) {
        Usuario u = buscarPorId(id);
        if (u == null) {
            return null;
        }

        if (nombre != null) u.setNombre(nombre);
        if (apellido != null) u.setApellido(apellido);
        if (fotoPerfil != null) u.setFotoPerfil(fotoPerfil);

        return em.merge(u);
    }

    public Usuario actualizarEstado(Long id, EstadoUsuario estado) {
        Usuario u = buscarPorId(id);
        if (u == null) {
            return null;
        }

        u.setEstado(estado);
        return em.merge(u);
    }
}