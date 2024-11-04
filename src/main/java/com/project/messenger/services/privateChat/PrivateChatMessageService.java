package com.project.messenger.services.privateChat;

import com.project.messenger.models.PrivateChat;
import com.project.messenger.models.PrivateChatMessage;
import com.project.messenger.models.UserProfile;
import com.project.messenger.models.enums.MessageStatus;
import com.project.messenger.repositories.PrivateChatMessageRepository;
import com.project.messenger.repositories.PrivateChatRepository;
import com.project.messenger.services.EncryptionService;
import com.project.messenger.services.UserProfileService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
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
    private final UserProfileService userProfileService;
    private final EncryptionService encryptionService;
    private final PrivateChatRepository privateChatRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private static final String MESSAGE_CACHE_PREFIX = "private chat messages:";
    private static final int CACHE_SIZE = 100;
    private final RedisTemplate redisTemplate;


    @Transactional
    public PrivateChatMessage sendMessage(int senderId, int privateChatId, String message) {
        PrivateChat privateChat = privateChatRepository.findById(privateChatId)
                .orElseThrow(() -> new RuntimeException("Чат не найден"));
        UserProfile sender = userProfileService.getUserProfile(senderId);
        UserProfile receiver = privateChat.getSender().getId() == senderId
                ? privateChat.getReceiver()
                : privateChat.getSender();
        String encryptedMessage = encryptionService.encrypt(message);
        PrivateChatMessage privateChatMessage = new PrivateChatMessage(privateChat, sender, receiver,
                LocalDateTime.now(), encryptedMessage, MessageStatus.SENT);
        privateChatMessageRepository.save(privateChatMessage);

        PrivateChatMessage redisMessage = new PrivateChatMessage(privateChatMessage.getId(), privateChat, sender, receiver,
                LocalDateTime.now(), message, privateChatMessage.getStatus());

        String cacheKey = MESSAGE_CACHE_PREFIX + privateChat.getId();
        redisTemplate.opsForList().rightPush(cacheKey, redisMessage);
        redisTemplate.opsForList().trim(cacheKey, 0, CACHE_SIZE - 1);
        messagingTemplate.convertAndSendToUser(
                String.valueOf(receiver.getId()),
                "/queue/private-chat",
                privateChatMessage
        );
        return privateChatMessage;
    }

    public List<PrivateChatMessage> getPrivateChatMessages(int privateChatId) {
        String cacheKey = MESSAGE_CACHE_PREFIX + privateChatId;

        List<PrivateChatMessage> cachedMessages = (List<PrivateChatMessage>) redisTemplate.opsForList().range(cacheKey, 0, -1);
        if (cachedMessages != null && !cachedMessages.isEmpty()) {
            return cachedMessages;
        }

        List<PrivateChatMessage> messages = privateChatMessageRepository
                .findByPrivateChatOrderBySentAtDesc(privateChatRepository.findById(privateChatId).get());
        for (PrivateChatMessage message : messages) {
            String decryptedMessageContent = encryptionService.decrypt(message.getMessage());
            message.setMessage(decryptedMessageContent);
        }
        redisTemplate.opsForList().rightPushAll(cacheKey, messages);
        redisTemplate.opsForList().trim(cacheKey, 0, CACHE_SIZE - 1);
        return messages;
    }

    @Transactional
    public void deletePrivateMessage(int messageId) {
        Optional<PrivateChatMessage> messageOptional = privateChatMessageRepository.findById(messageId);
        if (messageOptional.isPresent()) {
            PrivateChatMessage message = messageOptional.get();
            int chatId = message.getPrivateChat().getId();
            privateChatMessageRepository.deleteById(messageId);

            String cacheKey = MESSAGE_CACHE_PREFIX + chatId;
            redisTemplate.opsForList().remove(cacheKey, 0, message);

            messagingTemplate.convertAndSendToUser(String.valueOf(message.getReceiver()), "/queue/private-chat", "Message deleted");
        } else {
            throw new EntityNotFoundException("Message with messageId " + messageId + " not found");
        }
    }


    @Transactional
    public PrivateChatMessage editPrivateMessage(int messageId, String editedMessage) {
        Optional<PrivateChatMessage> privateChatMessage = privateChatMessageRepository.findById(messageId);
        if (privateChatMessage.isPresent()) {

            PrivateChatMessage message = privateChatMessage.get();
            int chatId = message.getPrivateChat().getId();
            String encryptedEditedMessage = encryptionService.encrypt(editedMessage);
            message.setMessage(encryptedEditedMessage);
            message.setStatus(MessageStatus.EDITED);
            PrivateChatMessage updatedMessage = privateChatMessageRepository.save(message);
            updatedMessage.setMessage(editedMessage);

            String cacheKey = MESSAGE_CACHE_PREFIX + chatId;
            redisTemplate.opsForList().set(cacheKey, redisTemplate.opsForList().indexOf(cacheKey, message), updatedMessage);
            messagingTemplate.convertAndSendToUser(String.valueOf(message.getReceiver().getId()), "/queue/private-chat", updatedMessage);

            return updatedMessage;
        } else {
            throw new EntityNotFoundException("Message not found with messageId: " + messageId);
        }
    }

    @Transactional
    public PrivateChatMessage markMessageAsRead(int id) {
        Optional<PrivateChatMessage> privateChatMessage = privateChatMessageRepository.findById(id);
        if (privateChatMessage.isPresent()) {
            PrivateChatMessage message = privateChatMessage.get();
            int chatId = message.getPrivateChat().getId();
            message.setStatus(MessageStatus.READ);
            PrivateChatMessage updatedMessage = privateChatMessageRepository.save(message);

            String cacheKey = MESSAGE_CACHE_PREFIX + chatId;
            redisTemplate.opsForList().set(cacheKey, redisTemplate.opsForList().indexOf(cacheKey, message), updatedMessage);
            messagingTemplate.convertAndSendToUser(String.valueOf(message.getReceiver().getId()), "/queue/private-chat", updatedMessage);

            return updatedMessage;
        } else {
            throw new EntityNotFoundException("Message not found with id: " + id);
        }
    }

}


//    @Transactional
//    public PrivateChatMessage sendMessage(int senderId, int receiverId, String message) {
//        UserProfile sender = userProfileService.getUserProfile(senderId);
//        UserProfile receiver = userProfileService.getUserProfile(receiverId);
//        PrivateChat privateChat = privateChatService.getPrivateChatBySenderAndReceiver(senderId, receiverId);
//        String encryptedMessage = encryptionService.encrypt(message);
//
//        PrivateChatMessage privateChatMessage = new PrivateChatMessage();
//        privateChatMessage.setPrivateChat(privateChat);
//        privateChatMessage.setSender(sender);
//        privateChatMessage.setReceiver(receiver);
//        privateChatMessage.setSentAt(LocalDateTime.now());
//        privateChatMessage.setMessage(encryptedMessage);
//        privateChatMessage.setStatus(MessageStatus.SENT);
//        PrivateChatMessage savedMessage = privateChatMessageRepository.save(privateChatMessage);
////        кеш редис
//        savedMessage.setMessage(message);
//        String cacheKey = MESSAGE_CACHE_PREFIX + privateChat.getId();
//        redisTemplate.opsForList().rightPush(cacheKey, savedMessage);
//        redisTemplate.opsForList().trim(cacheKey, 0, CACHE_SIZE - 1);
////        уведомляем о сообщении через вебсокет
//        messagingTemplate.convertAndSendToUser(String.valueOf(receiverId), "/queue/private-chat", savedMessage);
//        return savedMessage;
//    }