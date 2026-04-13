package com.appchat.repository;

import com.appchat.dto.ChatResumenDTO;
import com.appchat.model.Chat;
import com.appchat.model.Participa;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@ApplicationScoped
public class ChatRepository {

    @PersistenceContext(unitName = "appchatPU")
    private EntityManager em;

    public void guardar(Chat chat) {
        em.persist(chat);
    }

    public void guardarParticipacion(Participa participa) {
        em.persist(participa);
    }

    public List<ChatResumenDTO> listarChatsPorUsuario(Long userId) {
        return em.createQuery(
            "SELECT new com.appchat.dto.ChatResumenDTO(c.id, null, MAX(m.fechaEnvio)) " +
            "FROM Chat c " +
            "JOIN Participa p ON p.chat.id = c.id " +
            "LEFT JOIN Mensaje m ON m.chat.id = c.id " +
            "WHERE p.usuario.id = :userId " +
            "GROUP BY c.id " +
            "ORDER BY MAX(m.fechaEnvio) DESC",
            ChatResumenDTO.class)
            .setParameter("userId", userId)
            .getResultList();
    }

    public boolean esParticipante(Long chatId, Long userId) {
        Long total = em.createQuery(
            "SELECT COUNT(p) FROM Participa p WHERE p.chat.id = :chatId AND p.usuario.id = :userId",
            Long.class)
            .setParameter("chatId", chatId)
            .setParameter("userId", userId)
            .getSingleResult();
        return total > 0;
    }

    public Chat buscarDirectoEntre(Long userAId, Long userBId) {
        List<Chat> chats = em.createQuery(
            "SELECT p.chat FROM Participa p " +
            "WHERE p.usuario.id IN (:a, :b) " +
            "GROUP BY p.chat " +
            "HAVING COUNT(DISTINCT p.usuario.id) = 2 AND COUNT(p) = 2",
            Chat.class)
            .setParameter("a", userAId)
            .setParameter("b", userBId)
            .setMaxResults(1)
            .getResultList();
        return chats.isEmpty() ? null : chats.get(0);
    }
}
