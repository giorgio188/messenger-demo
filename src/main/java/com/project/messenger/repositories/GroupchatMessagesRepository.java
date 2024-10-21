package com.project.messenger.repositories;

import com.project.messenger.models.GroupChatMessages;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupchatMessagesRepository extends JpaRepository<GroupChatMessages, Integer> {
}
