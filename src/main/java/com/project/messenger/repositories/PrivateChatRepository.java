package com.project.messenger.repositories;

import com.project.messenger.models.PrivateChat;
import com.project.messenger.models.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PrivateChatRepository extends JpaRepository<PrivateChat, Integer> {
    Optional<PrivateChat> findPrivateChatById(int id);
    Optional<PrivateChat> findPrivateChatByMember(UserProfile member);
}
