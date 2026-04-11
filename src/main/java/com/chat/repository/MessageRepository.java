package com.chat.repository;

import com.chat.model.Message;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class MessageRepository {

    @PersistenceContext(unitName = "chatPU")
    private EntityManager em;

    @Transactional
    public Message save(Message message) {
        if (message.getId() == null) {
            em.persist(message);
            return message;
        }
        return em.merge(message);
    }

    public Optional<Message> findById(Long id) {
        return Optional.ofNullable(em.find(Message.class, id));
    }

    public List<Message> findByChatId(Long chatId) {
        TypedQuery<Message> query = em.createQuery(
            "SELECT m FROM Message m WHERE m.chat.id = :chatId ORDER BY m.sentAt ASC", Message.class);
        query.setParameter("chatId", chatId);
        return query.getResultList();
    }

    public List<Message> findBySenderId(Long senderId) {
        TypedQuery<Message> query = em.createQuery(
            "SELECT m FROM Message m WHERE m.sender.id = :senderId ORDER BY m.sentAt DESC", Message.class);
        query.setParameter("senderId", senderId);
        return query.getResultList();
    }

    @Transactional
    public void delete(Long id) {
        findById(id).ifPresent(em::remove);
    }
}
