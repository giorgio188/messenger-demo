package com.project.messenger.services;

import com.project.messenger.models.GroupChat;
import com.project.messenger.models.GroupChatMessage;
import com.project.messenger.models.UserProfile;
import com.project.messenger.models.enums.MessageStatus;
import com.project.messenger.repositories.GroupChatRepository;
import com.project.messenger.repositories.GroupchatMessagesRepository;
import com.project.messenger.repositories.UserProfileRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class GroupChatMessageService {

    private final UserProfileRepository userProfileRepository;
    private final UserProfileService userProfileService;
    private final GroupChatService groupChatService;
    private final EncryptionService encryptionService;
    private final GroupchatMessagesRepository groupchatMessagesRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private static final String MESSAGE_CACHE_PREFIX = "group chat messages:";
    private static final int CACHE_SIZE = 100;
    private final GroupChatRepository groupChatRepository;
    private RedisTemplate redisTemplate;


    @Transactional
    public GroupChatMessage sendMessage(int senderId, int groupChatId, String message) {
        UserProfile sender = userProfileService.getUserProfile(senderId);
        GroupChat groupChat = groupChatService.getGroupChat(groupChatId, senderId);
        String encryptedMessage = encryptionService.encrypt(message);

        GroupChatMessage groupChatMessage = new GroupChatMessage(
                groupChat,
                sender,
                LocalDateTime.now(),
                encryptedMessage,
                MessageStatus.SENT
        );

        GroupChatMessage savedMessage = groupchatMessagesRepository.save(groupChatMessage);

//        Кэш редис
        savedMessage.setMessage(message);
        String cacheKey = MESSAGE_CACHE_PREFIX + groupChat.getId();
        redisTemplate.opsForList().rightPush(cacheKey, savedMessage);
        redisTemplate.opsForList().trim(cacheKey, 0, CACHE_SIZE - 1);

//        уведомляем о сообщении через вебсокет
        messagingTemplate.convertAndSend("/queue/group-chat" + groupChat.getId(), groupChatMessage);
        return groupChatMessage;
    }

    public List<GroupChatMessage> getGroupChatMessages(int groupChatId) {
        String cacheKey = MESSAGE_CACHE_PREFIX + groupChatId;

        List<GroupChatMessage> cachedMessages = (List<GroupChatMessage>) redisTemplate.opsForList().range(cacheKey, 0, -1);
        if (cachedMessages != null && !cachedMessages.isEmpty()) {
            return cachedMessages;
        }

        List<GroupChatMessage> messages = groupchatMessagesRepository
                .findByGroupChatOrderBySentAtDesc(groupChatRepository.findById(groupChatId).get());
        for (GroupChatMessage message : messages) {
            String decryptedMessageContent = encryptionService.decrypt(message.getMessage());
            message.setMessage(decryptedMessageContent);

            if (message.getStatus() == MessageStatus.SENT) {
                message.setStatus(MessageStatus.READ);
                groupchatMessagesRepository.save(message);
            }
        }
        redisTemplate.opsForList().rightPushAll(cacheKey, messages);
        redisTemplate.opsForList().trim(cacheKey, 0, CACHE_SIZE - 1);
        return messages;
    }

    @Transactional
    public void deleteGroupMessage(int messageId) {
        Optional<GroupChatMessage> messageOptional = groupchatMessagesRepository.findById(messageId);
        if (messageOptional.isPresent()) {
            GroupChatMessage message = messageOptional.get();
            int chatId = message.getGroupChat().getId();
            groupchatMessagesRepository.deleteById(messageId);

            String cacheKey = MESSAGE_CACHE_PREFIX + chatId;
            redisTemplate.opsForList().remove(cacheKey, 0, message);

           messagingTemplate.convertAndSend("/queue/group-chat" + chatId,"Message deleted");
        } else {
            throw new EntityNotFoundException("Message with messageId " + messageId + " not found");
        }
    }

    @Transactional
    public GroupChatMessage editGroupMessage(int messageId, String editedMessage) {
        Optional<GroupChatMessage> groupChatMessage = groupchatMessagesRepository.findById(messageId);
        if (groupChatMessage.isPresent()) {

            GroupChatMessage message = groupChatMessage.get();
            int chatId = message.getGroupChat().getId();
            String encryptedEditedMessage = encryptionService.encrypt(editedMessage);
            message.setMessage(encryptedEditedMessage);
            message.setStatus(MessageStatus.EDITED);
            GroupChatMessage updatedMessage = groupchatMessagesRepository.save(message);
            updatedMessage.setMessage(editedMessage);

            String cacheKey = MESSAGE_CACHE_PREFIX + chatId;
            redisTemplate.opsForList().set(cacheKey, redisTemplate.opsForList().indexOf(cacheKey, message), updatedMessage);
            messagingTemplate.convertAndSend("/queue/group-chat" + chatId, updatedMessage);

            return updatedMessage;
        }
        else {
            throw new EntityNotFoundException("Message not found with messageId: " + messageId);
        }
    }
}
