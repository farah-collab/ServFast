package com.app.servicefinder.service;

import com.app.servicefinder.dto.message.*;
import com.app.servicefinder.model.*;
import com.app.servicefinder.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    @Transactional
    public MessageResponseDTO sendMessage(Long senderId, MessageRequest request) {
        User sender = userRepository.findById(senderId)
            .orElseThrow(() -> new RuntimeException("Sender not found"));
        User receiver = userRepository.findById(request.getReceiverId())
            .orElseThrow(() -> new RuntimeException("Receiver not found"));

        Message message = Message.builder()
            .sender(sender)
            .receiver(receiver)
            .content(request.getContent())
            .isRead(false)
            .build();

        return toDTO(messageRepository.saveAndFlush(message));
    }

    @Transactional(readOnly = true)
    public List<MessageResponseDTO> getConversation(Long userId, Long partnerId) {
        return messageRepository.findConversation(userId, partnerId)
            .stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional
    public List<MessageResponseDTO> getConversationAndMarkRead(Long userId, Long partnerId) {
        List<Message> messages = messageRepository.findConversation(userId, partnerId);
        messages.stream()
            .filter(m -> m.getReceiver().getId().equals(userId) && !m.getIsRead())
            .forEach(m -> m.setIsRead(true));
        messageRepository.saveAll(messages);
        return messages.stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ConversationDTO> getConversations(Long userId) {
        List<User> partners = messageRepository.findConversationPartners(userId);
        return partners.stream().map(partner -> {
            Message last = messageRepository.findLastMessage(userId, partner.getId()).orElse(null);
            Long unread = messageRepository.countBySender_IdAndReceiver_IdAndIsReadFalse(
                partner.getId(), userId);
            return ConversationDTO.builder()
                .participantId(partner.getId())
                .participantName(partner.getFirstName() + " " + partner.getLastName())
                .participantPhoto(partner.getProfilePhoto())
                .lastMessage(last != null ? last.getContent() : "")
                .lastMessageAt(last != null ? last.getSentAt() : null)
                .unreadCount(unread)
                .build();
        }).collect(Collectors.toList());
    }

    private MessageResponseDTO toDTO(Message m) {
        return MessageResponseDTO.builder()
            .id(m.getId())
            .senderId(m.getSender().getId())
            .senderName(m.getSender().getFirstName() + " " + m.getSender().getLastName())
            .senderPhoto(m.getSender().getProfilePhoto())
            .receiverId(m.getReceiver().getId())
            .receiverName(m.getReceiver().getFirstName() + " " + m.getReceiver().getLastName())
            .content(m.getContent())
            .isRead(m.getIsRead())
            .sentAt(m.getSentAt())
            .build();
    }
}