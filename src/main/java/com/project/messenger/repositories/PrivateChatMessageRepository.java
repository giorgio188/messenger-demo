package com.project.messenger.repositories;

import com.project.messenger.models.PrivateChat;
import com.project.messenger.models.PrivateChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PrivateChatMessageRepository extends JpaRepository<PrivateChatMessage, Integer> {
    List<PrivateChatMessage> findByPrivateChat(PrivateChat privateChat);
    List<PrivateChatMessage> findByPrivateChatOrderBySentAtDesc(PrivateChat privateChat);
}
