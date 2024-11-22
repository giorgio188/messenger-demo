package com.project.messenger.repositories;

import com.project.messenger.models.PrivateChat;
import com.project.messenger.models.PrivateChatMessage;
import com.project.messenger.models.enums.MessageStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PrivateChatMessageRepository extends JpaRepository<PrivateChatMessage, Integer> {
    List<PrivateChatMessage> findByPrivateChatOrderBySentAtDesc(PrivateChat privateChat);
    List<PrivateChatMessage> findByPrivateChatAndStatusAndReceiverIdAndSenderIdNot(
            PrivateChat privateChat,
            MessageStatus status,
            int receiverId,
            int senderId
    );
}
