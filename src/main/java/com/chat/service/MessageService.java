package com.chat.service;

import com.chat.dto.MessageDTO;
import com.chat.model.Chat;
import com.chat.model.Message;
import com.chat.model.User;
import com.chat.repository.ChatRepository;
import com.chat.repository.MessageRepository;
import com.chat.repository.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class MessageService {

    @Inject
    private MessageRepository messageRepository;

    @Inject
    private UserRepository userRepository;

    @Inject
    private ChatRepository chatRepository;

    public MessageDTO sendMessage(Long senderId, Long chatId, String content) {
        User sender = userRepository.findById(senderId)
            .orElseThrow(() -> new IllegalArgumentException("Sender not found"));
        Chat chat = chatRepository.findById(chatId)
            .orElseThrow(() -> new IllegalArgumentException("Chat not found"));

        Message message = new Message(content, sender, chat);
        Message saved = messageRepository.save(message);
        return toDTO(saved);
    }

    public List<MessageDTO> getMessagesByChat(Long chatId) {
        return messageRepository.findByChatId(chatId).stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }

    public List<MessageDTO> getMessagesBySender(Long senderId) {
        return messageRepository.findBySenderId(senderId).stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }

    private MessageDTO toDTO(Message message) {
        return new MessageDTO(
            message.getId(),
            message.getContent(),
            message.getSender().getId(),
            message.getSender().getUsername(),
            message.getChat().getId(),
            message.getSentAt()
        );
    }
}
