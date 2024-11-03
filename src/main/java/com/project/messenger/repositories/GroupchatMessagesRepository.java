package com.project.messenger.repositories;

import com.project.messenger.models.GroupChat;
import com.project.messenger.models.GroupChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupchatMessagesRepository extends JpaRepository<GroupChatMessage, Integer> {
    List<GroupChatMessage> findByGroupChatOrderBySentAtDesc(GroupChat groupChat);
}
