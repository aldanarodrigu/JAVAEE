package com.chat.service;

import com.chat.model.*;
import com.chat.repository.ChatRepository;
import com.chat.repository.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class ChatService {

    @Inject
    private ChatRepository chatRepository;

    @Inject
    private UserRepository userRepository;

    @PersistenceContext(unitName = "chatPU")
    private EntityManager em;

    public ChatDirect getOrCreateDirectChat(Long user1Id, Long user2Id) {
        Optional<ChatDirect> existing = chatRepository.findDirectChatByUsers(user1Id, user2Id);
        if (existing.isPresent()) {
            return existing.get();
        }
        User user1 = userRepository.findById(user1Id)
            .orElseThrow(() -> new IllegalArgumentException("User1 not found"));
        User user2 = userRepository.findById(user2Id)
            .orElseThrow(() -> new IllegalArgumentException("User2 not found"));

        ChatDirect chat = new ChatDirect(user1, user2);
        return chatRepository.save(chat);
    }

    public List<ChatDirect> getDirectChatsByUser(Long userId) {
        return chatRepository.findDirectChatsByUserId(userId);
    }

    @Transactional
    public ChatGroup createGroupChat(String name, String description, Long ownerId, List<Long> memberIds) {
        User owner = userRepository.findById(ownerId)
            .orElseThrow(() -> new IllegalArgumentException("Owner not found"));

        Group group = new Group(name, description, owner);
        group.getMembers().add(owner);

        for (Long memberId : memberIds) {
            userRepository.findById(memberId).ifPresent(group.getMembers()::add);
        }

        em.persist(group);

        ChatGroup chatGroup = new ChatGroup(group);
        em.persist(chatGroup);
        return chatGroup;
    }

    public Optional<ChatGroup> getGroupChat(Long groupId) {
        return chatRepository.findGroupChatByGroupId(groupId);
    }

    public Optional<Chat> findChatById(Long chatId) {
        return chatRepository.findById(chatId);
    }
}
