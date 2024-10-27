package com.project.messenger.repositories;

import com.project.messenger.models.PrivateChatFiles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PrivateChatFilesRepository extends JpaRepository<PrivateChatFiles, Integer> {
    Optional<PrivateChatFiles> findByPrivateChatId(int privateChatId);
}
