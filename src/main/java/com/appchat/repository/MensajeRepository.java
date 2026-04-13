package com.appchat.repository;

import com.appchat.dto.MensajeDTO;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@ApplicationScoped
public class MensajeRepository {

    @PersistenceContext(unitName = "appchatPU")
    private EntityManager em;

    public List<MensajeDTO> historialPaginado(Long chatId, int page, int size) {
        return em.createQuery(
            "SELECT new com.appchat.dto.MensajeDTO(m.id, m.chat.id, m.remitente.id, m.fechaEnvio, m.tipo, m.estado, m.contenido) " +
            "FROM Mensaje m WHERE m.chat.id = :chatId ORDER BY m.fechaEnvio DESC",
            MensajeDTO.class)
            .setParameter("chatId", chatId)
            .setFirstResult(page * size)
            .setMaxResults(size)
            .getResultList();
    }

    public long contarPorChat(Long chatId) {
        return em.createQuery(
            "SELECT COUNT(m) FROM Mensaje m WHERE m.chat.id = :chatId",
            Long.class)
            .setParameter("chatId", chatId)
            .getSingleResult();
    }
}
