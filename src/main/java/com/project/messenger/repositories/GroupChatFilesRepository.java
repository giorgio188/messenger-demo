package com.project.messenger.repositories;

import com.project.messenger.models.GroupChatFiles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupChatFilesRepository extends JpaRepository<GroupChatFiles, Integer> {
}
