package com.project.messenger.repositories;

import com.project.messenger.models.GroupChat;
import com.project.messenger.models.GroupChatMessages;
import com.project.messenger.models.PrivateChat;
import com.project.messenger.models.PrivateChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupchatMessagesRepository extends JpaRepository<GroupChatMessages, Integer> {
    List<GroupChatMessages> findByGroupChatOrderBySentAtDesc(GroupChat groupChat);
}
