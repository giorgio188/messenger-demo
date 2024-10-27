package com.project.messenger.controllers;

import com.project.messenger.dto.GroupChatDTO;
import com.project.messenger.models.GroupChat;
import com.project.messenger.models.UserProfile;
import com.project.messenger.models.enums.Roles;
import com.project.messenger.services.GroupChatService;
import com.project.messenger.services.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RequestMapping("/group-chat")
@RestController
@RequiredArgsConstructor
public class GroupChatController {


    private final GroupChatService groupChatService;
    private final UserProfileService userProfileService;

    @PostMapping("/create")
    public ResponseEntity<GroupChat> createGroupChat(@RequestBody GroupChatDTO groupChatDTO) {
        String name = groupChatDTO.getName();
        String description = groupChatDTO.getDescription();
        Integer id = groupChatDTO.getCreatorId();

        GroupChat groupChat = groupChatService.createGroupChat(name, description, id);
        return ResponseEntity.status(HttpStatus.CREATED).body(groupChat);
    }

    @PatchMapping("/{groupChatId}/add-user")
    public Map<String, String> addUserToGroupChat(
            @PathVariable Integer groupChatId,
            @RequestBody Integer groupChatMemberId
    ) {
        groupChatService.addUser(groupChatId, groupChatMemberId, Roles.MEMBER);
        UserProfile user = userProfileService.getUserProfile(groupChatMemberId);
        GroupChat groupChat = groupChatService.getGroupChat(groupChatId);
        return Map.of(user.getNickname(),"Now in " + groupChat.getName());
    }


}
