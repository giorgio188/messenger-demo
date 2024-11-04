package com.project.messenger.repositories;

import com.project.messenger.models.GroupChat;
import com.project.messenger.models.GroupChatMembers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupChatRepository extends JpaRepository<GroupChat, Integer> {

    List<GroupChatMembers> findAllGroupChatMember(int groupChetId);


}
