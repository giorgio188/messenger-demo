package com.project.messenger.services;

import com.project.messenger.models.PrivateChat;
import com.project.messenger.models.PrivateChatMessage;
import com.project.messenger.models.UserProfile;
import com.project.messenger.models.enums.MessageStatus;
import com.project.messenger.repositories.PrivateChatMessageRepository;
import com.project.messenger.repositories.PrivateChatRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PrivateChatMessageService {

    private final PrivateChatMessageRepository privateChatMessageRepository;
    private final PrivateChatService privateChatService;
    private final UserProfileService userProfileService;
    private final EncryptionService encryptionService;
    private final PrivateChatRepository privateChatRepository;

    @Transactional
    public PrivateChatMessage sendMessage(int senderId, int receiverId, String message) {
        UserProfile sender = userProfileService.getUserProfile(senderId);
        UserProfile receiver = userProfileService.getUserProfile(receiverId);
        PrivateChat privateChat = privateChatService.getPrivateChat(senderId, receiverId);
        String encryptedMessage = encryptionService.encrypt(message);

        PrivateChatMessage privateChatMessage = new PrivateChatMessage();
        privateChatMessage.setPrivateChat(privateChat);
        privateChatMessage.setSender(sender);
        privateChatMessage.setReceiver(receiver);
        privateChatMessage.setSentAt(LocalDateTime.now());
        privateChatMessage.setMessage(encryptedMessage);
        privateChatMessage.setStatus(MessageStatus.SENT);
        return privateChatMessageRepository.save(privateChatMessage);
    }

    public List<PrivateChatMessage> getPrivateChatMessages(int privateChatId) {
        List<PrivateChatMessage> messages = privateChatMessageRepository
                .findByPrivateChat(privateChatRepository.findById(privateChatId).get());
        for (PrivateChatMessage message : messages) {
            String decryptedMessageContent = encryptionService.decrypt(message.getMessage());
            message.setMessage(decryptedMessageContent);
        }
        return messages;
    }

    @Transactional
    public void deletePrivateMessage(int id) {
        privateChatMessageRepository.deleteById(id);
    }

    @Transactional
    public PrivateChatMessage editPrivateMessage(int id, String editedMessage) {
        Optional<PrivateChatMessage> privateChatMessage = privateChatMessageRepository.findById(id);
        if (privateChatMessage.isPresent()) {
            PrivateChatMessage message = privateChatMessage.get();
            String encryptedEditedMessage = encryptionService.encrypt(editedMessage);
            message.setMessage(encryptedEditedMessage);
            message.setStatus(MessageStatus.EDITED);
            return privateChatMessageRepository.save(message);
        }
        else {
            throw new EntityNotFoundException("Message not found with id: " + id);
        }
    }

}
