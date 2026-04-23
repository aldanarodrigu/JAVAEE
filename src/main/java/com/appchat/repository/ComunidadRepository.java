
package com.appchat.repository;

import com.appchat.model.Comunidad;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

public class ComunidadRepository {

    @PersistenceContext(unitName = "appchatPU") 
    private EntityManager em;
    
    public Comunidad buscarPorId(Long comunidadId) {
        return em.find(Comunidad.class, comunidadId);
    }

    public boolean sonMiembros(Long comunidadId, Long u1, Long u2) {

        Long count = em.createQuery("""
            SELECT COUNT(mc) FROM MiembroComunidad mc
            WHERE mc.comunidad.id = :comunidadId
            AND mc.usuario.id IN (:u1, :u2)
        """, Long.class)
        .setParameter("comunidadId", comunidadId)
        .setParameter("u1", u1)
        .setParameter("u2", u2)
        .getSingleResult();

        return count == 2;
    }

    public void guardar(Comunidad comunidadNueva) {
        em.persist(comunidadNueva);
    }
    
}
