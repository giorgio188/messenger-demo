package com.project.messenger.services.groupChat;

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

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GroupChatService {

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

        addUser(groupChat.getId(), creatorId, Roles.CREATOR);
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
    public void editDescription(int groupChatId, String description, int userId) {
        if (isAdmin(groupChatId, userId)) {
            GroupChat groupChat = groupChatRepository.findById(groupChatId).get();
            groupChat.setDescription(description);
            groupChatRepository.save(groupChat);
        }
    }

    @Transactional
    public void editName(int groupChatId, String name, int userId) {
        if (isAdmin(groupChatId, userId)) {
            GroupChat groupChat = groupChatRepository.findById(groupChatId).get();
            groupChat.setName(name);
            groupChatRepository.save(groupChat);
        } throw new AccessDeniedException("User is not an admin of this chat");
    }

    @Transactional
    public void deleteUser(int groupChatId, int userToDeleteId, int userId) {
        if (isAdmin(groupChatId, userId)) {
            GroupChat groupChat = groupChatRepository.findById(groupChatId).get();
            UserProfile user = userProfileRepository.findById(userToDeleteId).get();
            GroupChatMembers member = groupChatMembersRepository.findByGroupChatAndMember(groupChat, user);
            groupChatMembersRepository.delete(member);
        } throw new AccessDeniedException("User is not an admin of this chat");
    }

    public List<GroupChat> getAllGroupChatsByUser(int userId) {
        UserProfile user = userProfileRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found"));
        List<GroupChatMembers> members = groupChatMembersRepository.findByMember(user);
        return members.stream()
                .map(GroupChatMembers::getGroupChat)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteGroupChat(int groupChatId, int userId) {
        if (isCreator(groupChatId, userId)) {
            groupChatRepository.deleteById(groupChatId);
            List<GroupChatMembers> members = getAllGroupChatMembersByGroupChat(groupChatId);
            groupChatMembersRepository.deleteAllByGroupChat(groupChatRepository.findById(groupChatId).get());
        } else throw new AccessDeniedException("User is not an admin of this chat");
    }

    public List<GroupChatMembers> getAllGroupChatMembersByGroupChat(int groupChatId) {
        GroupChat groupChat = groupChatRepository.findById(groupChatId).get();
        List<GroupChatMembers> members = groupChatMembersRepository.findAllByGroupChat(groupChat);
        return members;
    }

    @Transactional
    public void leaveGroupChat(int groupChatId, int memberId) {
        UserProfile member = userProfileRepository.findById(memberId).get();
        GroupChat groupChat = groupChatRepository.findById(groupChatId).get();
        groupChatMembersRepository.deleteByGroupChatAndMember(groupChat, member);
    }

    @Transactional
    public void setRoleToMember(int groupChatId, int memberId, Roles role, int adminId) {
        if (isAdmin(groupChatId, adminId)) {
            UserProfile member = userProfileRepository.findById(memberId).get();
            GroupChat groupChat = groupChatRepository.findById(groupChatId).get();
            GroupChatMembers memberToChange = groupChatMembersRepository.findByGroupChatAndMember(groupChat, member);
            memberToChange.setRole(role);
            groupChatMembersRepository.save(memberToChange);
        } else throw new AccessDeniedException("User is not an admin of this chat");
    }

    private boolean isAdmin (int groupChatId, int userId) {
        GroupChat groupChat = groupChatRepository.findById(groupChatId).get();
        GroupChatMembers admin = groupChatMembersRepository.findByGroupChatAndMember(groupChat,
                userProfileRepository.findById(userId).get());
        if (admin.getRole() == Roles.ADMIN || admin.getRole() == Roles.CREATOR) {
            return true;
        } else return false;
    }

    private boolean isCreator(int groupChatId, int userId) {
        GroupChat groupChat = groupChatRepository.findById(groupChatId).get();
        GroupChatMembers creator = groupChatMembersRepository.findByGroupChatAndMember(groupChat,
                userProfileRepository.findById(userId).get());
        if (creator.getRole() == Roles.CREATOR) {
            return true;
        } else return false;
    }

}

