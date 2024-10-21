package com.project.messenger.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PrivateChatFiles extends JpaRepository<PrivateChatFiles, Integer> {
    Optional<PrivateChatFiles> findByPrivateChatId(int privateChatId);
}
