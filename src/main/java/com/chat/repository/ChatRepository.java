package com.chat.repository;

import com.chat.model.Chat;
import com.chat.model.ChatDirect;
import com.chat.model.ChatGroup;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class ChatRepository {

    @PersistenceContext(unitName = "chatPU")
    private EntityManager em;

    @Transactional
    public <T extends Chat> T save(T chat) {
        if (chat.getId() == null) {
            em.persist(chat);
            return chat;
        }
        return em.merge(chat);
    }

    public Optional<Chat> findById(Long id) {
        return Optional.ofNullable(em.find(Chat.class, id));
    }

    public Optional<ChatDirect> findDirectChatByUsers(Long user1Id, Long user2Id) {
        TypedQuery<ChatDirect> query = em.createQuery(
            "SELECT cd FROM ChatDirect cd WHERE " +
            "(cd.user1.id = :user1Id AND cd.user2.id = :user2Id) OR " +
            "(cd.user1.id = :user2Id AND cd.user2.id = :user1Id)",
            ChatDirect.class);
        query.setParameter("user1Id", user1Id);
        query.setParameter("user2Id", user2Id);
        return query.getResultStream().findFirst();
    }

    public List<ChatDirect> findDirectChatsByUserId(Long userId) {
        TypedQuery<ChatDirect> query = em.createQuery(
            "SELECT cd FROM ChatDirect cd WHERE cd.user1.id = :userId OR cd.user2.id = :userId",
            ChatDirect.class);
        query.setParameter("userId", userId);
        return query.getResultList();
    }

    public Optional<ChatGroup> findGroupChatByGroupId(Long groupId) {
        TypedQuery<ChatGroup> query = em.createQuery(
            "SELECT cg FROM ChatGroup cg WHERE cg.group.id = :groupId", ChatGroup.class);
        query.setParameter("groupId", groupId);
        return query.getResultStream().findFirst();
    }

    @Transactional
    public void delete(Long id) {
        findById(id).ifPresent(em::remove);
    }
}
