package com.project.messenger.repositories;

import com.project.messenger.models.PrivateChatMessages;
import com.project.messenger.models.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PrivateChatMessagesRepository extends JpaRepository<PrivateChatMessages, Integer> {
    Optional<UserProfile> findBySenderAndReceiver(UserProfile sender, UserProfile receiver);
}
