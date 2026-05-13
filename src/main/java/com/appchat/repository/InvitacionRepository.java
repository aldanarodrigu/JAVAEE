package com.appchat.repository;

import com.appchat.model.InvitacionComunidad;
import com.appchat.model.enums.EstadoInvitacion;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@ApplicationScoped
public class InvitacionRepository {
    
    @PersistenceContext(unitName = "appchatPU")
    private EntityManager em;
    
    public void guardar(InvitacionComunidad ic){
        em.persist(ic);
    }
    
    public boolean existeInvitacionPendiente(Long comunidadId, Long userId){
        Long count = em.createQuery(
                "SELECT COUNT(i) FROM InvitacionComunidad i " +
                "WHERE i.comunidad = :comunidadId " +
                "AND i.usuarioInvitadoId = :userId " +
                "AND i.estado = :estado", Long.class)
                .setParameter("comunidadId", comunidadId)
                .setParameter("userId", userId)
                .setParameter("estado", EstadoInvitacion.PENDIENTE)
                .getSingleResult();
        
        return count > 0;
    }
    
}
