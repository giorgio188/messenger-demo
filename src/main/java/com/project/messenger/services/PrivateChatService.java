package com.project.messenger.services;

import com.project.messenger.dto.PrivateChatDTO;
import com.project.messenger.models.PrivateChat;
import com.project.messenger.models.UserProfile;
import com.project.messenger.repositories.PrivateChatRepository;
import com.project.messenger.repositories.UserProfileRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PrivateChatService {

    private final UserProfileRepository userProfileRepository;
    private final PrivateChatRepository privateChatRepository;
    private final SimpMessagingTemplate messagingTemplate;


    public PrivateChat getPrivateChatBySenderAndReceiver(int senderId, int receiverId) {
        UserProfile sender = userProfileRepository.findById(senderId).orElse(null);
        UserProfile receiver = userProfileRepository.findById(receiverId).orElse(null);

        PrivateChat privateChat = privateChatRepository.findPrivateChatBySenderAndReceiver(sender, receiver);
        if (privateChat == null) {
            privateChat = privateChatRepository.findPrivateChatBySenderAndReceiver(receiver, sender);
        }
        if (privateChat == null) {
            throw new EntityNotFoundException("Private chat not found");
        }
        return privateChat;
    }

    public PrivateChat getPrivateChat(int chatId, int senderId) throws AccessDeniedException {
        PrivateChat privateChat = privateChatRepository.findById(chatId)
                .orElseThrow(() -> new EntityNotFoundException("Private chat not found"));

        if (privateChat.getSender().getId() != senderId &&
                privateChat.getReceiver().getId() != senderId) {
            throw new AccessDeniedException("User is not a participant of this chat");
        }
        return privateChat;
    }

    // Метод для получения ID чата по участникам
    public int getChatId(int senderId, int receiverId) {
        UserProfile sender = userProfileRepository.findById(senderId)
                .orElseThrow(() -> new EntityNotFoundException("Sender not found"));
        UserProfile receiver = userProfileRepository.findById(receiverId)
                .orElseThrow(() -> new EntityNotFoundException("Receiver not found"));

        PrivateChat privateChat = privateChatRepository.findPrivateChatBySenderAndReceiver(sender, receiver);
        if (privateChat == null) {
            privateChat = privateChatRepository.findPrivateChatBySenderAndReceiver(receiver, sender);
        }
        if (privateChat == null) {
            throw new EntityNotFoundException("Private chat not found");
        }
        return privateChat.getId();
    }

    public List<PrivateChat> getAllChatsOfOneUser(int id) {
        Optional<UserProfile> userProfile = userProfileRepository.findById(id);
        List<PrivateChat> allChatsAsSender = privateChatRepository.findPrivateChatBySender(userProfile.orElse(null));
        List<PrivateChat> allChatsAsReceiver = privateChatRepository.findPrivateChatByReceiver(userProfile.orElse(null));
        List<PrivateChat> allChats = new ArrayList<>();
        allChats.addAll(allChatsAsSender);
        allChats.addAll(allChatsAsReceiver);
        return allChats;
    }

    @Transactional
    public void createPrivateChat(int senderId, int receiverId) {
        UserProfile sender = userProfileRepository.findById(senderId).orElse(null);
        UserProfile receiver = userProfileRepository.findById(receiverId).orElse(null);
        PrivateChat privateChat = new PrivateChat(sender, receiver, LocalDateTime.now());
        PrivateChat savedChat = privateChatRepository.save(privateChat);
        messagingTemplate.convertAndSend(
                "/topic/chats/" + senderId,
                convertToDto(savedChat)
        );

        messagingTemplate.convertAndSend(
                "/topic/chats/" + receiverId,
                convertToDto(savedChat)
        );
    }

    @Transactional
    public void deletePrivateChat(int id) {
        privateChatRepository.deleteById(id);
    }

    private PrivateChatDTO convertToDto(PrivateChat chat) {
        PrivateChatDTO dto = new PrivateChatDTO();
        dto.setId(chat.getId());
        dto.setSenderId(chat.getSender().getId());
        dto.setSenderUsername(chat.getSender().getUsername());
        dto.setReceiverId(chat.getReceiver().getId());
        dto.setReceiverUsername(chat.getReceiver().getUsername());
        dto.setCreatedAt(chat.getCreatedAt());
        return dto;
    }

}
