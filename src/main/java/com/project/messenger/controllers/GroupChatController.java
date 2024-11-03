package com.project.messenger.controllers;

import com.project.messenger.dto.GroupChatDTO;
import com.project.messenger.models.GroupChat;
import com.project.messenger.models.UserProfile;
import com.project.messenger.models.enums.Roles;
import com.project.messenger.security.JWTUtil;
import com.project.messenger.services.GroupChatService;
import com.project.messenger.services.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Map;

@RequestMapping("api/group-chat")
@RestController
@RequiredArgsConstructor
public class GroupChatController {


    private final GroupChatService groupChatService;
    private final JWTUtil jwtUtil;

    @GetMapping("/{groupChatId}")
    public ResponseEntity<GroupChat> getGroupChat(
            @RequestHeader("Authorization") String token,
            @PathVariable int groupChatId
    )  throws AccessDeniedException {
        int memberId = jwtUtil.extractUserId(token.replace("Bearer ", ""));
        GroupChat groupChat = groupChatService.getGroupChat(groupChatId, memberId);
        return  ResponseEntity.ok(groupChat);
    }

    @GetMapping()
    public ResponseEntity<List<GroupChat>> getGroupChatsByMember(
            @RequestHeader("Authorization") String token
    ) {
        int memberId = jwtUtil.extractUserId(token.replace("Bearer ", ""));
        List<GroupChat> groupChats = groupChatService.getAllGroupChatsByUser(memberId);
        return  ResponseEntity.ok(groupChats);
    }

    @PostMapping("/create")
    public ResponseEntity<GroupChat> createGroupChat(
            @RequestHeader("Authorization") String token,
            @RequestBody GroupChatDTO groupChatDTO
    ) {
        int creatorId = jwtUtil.extractUserId(token.replace("Bearer ", ""));
        groupChatService.createGroupChat(
                groupChatDTO.getName(),
                groupChatDTO.getDescription(),
                creatorId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{groupChatId}")
    @ResponseStatus(HttpStatus.FOUND)
    public String deleteGroupChat(@PathVariable int groupChatId) {
        groupChatService.deleteGroupChat(groupChatId);
        return "redirect:/all";
    }

    @PatchMapping("/{groupChatId}}/edit-descrip")
    public ResponseEntity<GroupChat> editGroupChatDescription(
            @PathVariable int groupChatId,
            @RequestParam String newDesc
    ) {
        groupChatService.editDescription(groupChatId, newDesc);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{groupChatId}/edit-name")
    public ResponseEntity<GroupChat> editGroupChatName(
            @PathVariable int groupChatId,
            @RequestParam String newName
    ) {
        groupChatService.editName(groupChatId, newName);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{groupChatId}/delete-user")
    public ResponseEntity<GroupChat> deleteUser(
            @PathVariable int groupChatId,
            @RequestParam int userId
    ) {
        groupChatService.deleteUser(groupChatId, userId);
        return ResponseEntity.ok().build();
    }

}
