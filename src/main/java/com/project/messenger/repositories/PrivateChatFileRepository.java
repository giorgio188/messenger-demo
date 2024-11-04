package com.project.messenger.repositories;

import com.project.messenger.models.PrivateChat;
import com.project.messenger.models.PrivateChatFiles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PrivateChatFileRepository extends JpaRepository<PrivateChatFiles, Integer> {
    PrivateChatFiles findByPrivateChatId(int privateChatId);
    List<PrivateChatFiles> findByPrivateChatOrderBySentAtDesc(PrivateChat privateChat);
}
