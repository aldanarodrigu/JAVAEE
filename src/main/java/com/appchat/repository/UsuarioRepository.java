package com.appchat.repository;

import com.appchat.model.Usuario;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import java.util.List;

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
                .setParameter("email", email) //le asigna email a email para evitar inyeccion
                .getSingleResult(); //espera un solo resultado
        } catch (NoResultException e) {
            return null;
        }
    }
    
    public void eliminar(Long id){
        Usuario usuario = em.find(Usuario.class, id);
        if(usuario != null){
            em.remove(usuario);
        }
    }
    
    public Usuario actualizar(Usuario usuario){
        return em.merge(usuario);
    }
    
    public boolean existeUsuario(Long id){
        return em.find(Usuario.class, id) != null;
    }
    
    public List<Usuario> listarUsuarios(){
        return em.createQuery("SELECT u FROM Usuario u", Usuario.class).getResultList();
    }
    
    public List<Usuario> buscarPorNombres(String nombre){
        return em.createQuery("SELECT u FROM Usuario u WHERE u.nombre LIKE :nombre", Usuario.class)
                .setParameter("nombre", "%" + nombre + "%")
                .getResultList();
    }
    
    public Usuario buscarPorId(Long id){
        return em.find(Usuario.class, id);
    }
    
    public boolean existePorEmail(String email){
        try {
            em.createQuery(
                "SELECT 1 FROM Usuario u WHERE u.email = :email")
                .setParameter("email", email)
                .getSingleResult();
            return true;
        } catch (NoResultException e) {
            return false;
        }
    }
}
