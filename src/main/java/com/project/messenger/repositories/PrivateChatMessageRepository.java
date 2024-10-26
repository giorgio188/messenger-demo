package com.project.messenger.repositories;

import com.project.messenger.models.PrivateChat;
import com.project.messenger.models.PrivateChatMessage;
import com.project.messenger.models.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PrivateChatMessageRepository extends JpaRepository<PrivateChatMessage, Integer> {
    List<PrivateChatMessage> findByPrivateChat(PrivateChat privateChat);
    List<PrivateChatMessage> findByPrivateChatOrderBySentAtDesc(PrivateChat privateChat);
}
