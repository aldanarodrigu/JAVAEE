package com.appchat.repository;

import com.appchat.model.Usuario;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@ApplicationScoped 
public class UsuarioRepository {

    @PersistenceContext(unitName = "appchatPU") //como que inyecta al entitymanager 
    private EntityManager em;

    public void guardar(Usuario usuario) {
        em.persist(usuario);
    }

    public Usuario buscarPorEmail(String email){
        try {
            return em.createQuery(
                "SELECT u FROM Usuario u WHERE u.email = :email", Usuario.class)
                .setParameter("email", email)
                .getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }
    
    public void eliminar(Long id){
        Usuario usuario = em.find(Usuario.class, id);
        if(usuario != null){
            em.remove(id);
        }
    }
    
    public Usuario actualizar(Usuario usuario){
        return em.merge(usuario);
    }
    
    // VERIFICAR SI EXISTE
    
    // BUSCAR POR NOMBRE
    
    // PAGINACION
    
    // ETC
}
