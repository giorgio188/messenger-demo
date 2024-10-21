package com.project.messenger.repositories;

import com.project.messenger.models.GroupChatMembers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupChatMembersRepository extends JpaRepository<GroupChatMembers, Integer> {

}
