package com.project.messenger.services;

import com.project.messenger.models.GroupChat;
import com.project.messenger.models.GroupChatMembers;
import com.project.messenger.models.UserProfile;
import com.project.messenger.models.enums.Roles;
import com.project.messenger.repositories.GroupChatMembersRepository;
import com.project.messenger.repositories.GroupChatRepository;
import com.project.messenger.repositories.UserProfileRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GroupChatService {

    private final UserProfileService userProfileService;
    private final GroupChatRepository groupChatRepository;
    private final UserProfileRepository userProfileRepository;
    private final GroupChatMembersRepository groupChatMembersRepository;

    @Transactional
    public void addUser(int group, int userId, Roles role) {
        GroupChat groupChat = groupChatRepository.findById(group).get();
        UserProfile user = userProfileRepository.findById(userId).get();
        GroupChatMembers member = new GroupChatMembers();
        member.setMember(user);
        member.setGroupChat(groupChat);
        member.setRole(role);
        groupChatMembersRepository.save(member);
    }

    @Transactional
    public GroupChat createGroupChat(String groupName, String description, int creatorId) {
        GroupChat groupChat = new GroupChat();
        groupChat.setName(groupName);
        groupChat.setDescription(description);
        groupChat.setCreatedAt(LocalDateTime.now());
        groupChatRepository.save(groupChat);

        addUser(groupChat.getId(), creatorId, Roles.ADMIN);
        return groupChat;
    }


    public GroupChat getGroupChat(int groupChatId, int memberId) throws AccessDeniedException {
        GroupChat groupChat = groupChatRepository.findById(groupChatId)
                .orElseThrow(() -> new EntityNotFoundException("GroupChat not found"));
        if (groupChat.getGroupChatMembers().contains(memberId)) {
            return groupChat;

        } else {
            throw new AccessDeniedException("User is not a participant of this chat");
        }

    }

    @Transactional
    public void editDescription(int groupChatId, String description) {
        GroupChat groupChat = groupChatRepository.findById(groupChatId).get();
        groupChat.setDescription(description);
        groupChatRepository.save(groupChat);
    }

    @Transactional
    public void editName(int groupChatId, String name) {
        GroupChat groupChat = groupChatRepository.findById(groupChatId).get();
        groupChat.setName(name);
        groupChatRepository.save(groupChat);
    }

    @Transactional
    public void deleteUser(int groupChatId, int userId) {
        GroupChat groupChat = groupChatRepository.findById(groupChatId).get();
        UserProfile user = userProfileRepository.findById(userId).get();
        GroupChatMembers member = groupChatMembersRepository.findByGroupChatAndMember(groupChat, user);
        groupChatMembersRepository.delete(member);
    }

    public List<GroupChat> getAllGroupChatsByUser(int userId) {
        UserProfile user = userProfileRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found"));
        List<GroupChatMembers> members = groupChatMembersRepository.findByMember(user);
        return members.stream()
                .map(GroupChatMembers::getGroupChat)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteGroupChat(int groupChatId) {
        groupChatRepository.deleteById(groupChatId);
    }


}

