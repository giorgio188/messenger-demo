package com.project.messenger.controllers;

import com.project.messenger.dto.GroupChatDTO;
import com.project.messenger.models.GroupChat;
import com.project.messenger.models.GroupChatMembers;
import com.project.messenger.models.enums.Roles;
import com.project.messenger.security.JWTUtil;
import com.project.messenger.services.groupChat.GroupChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.List;

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
    public String deleteGroupChat(@RequestHeader("Authorization") String token,
                                  @PathVariable int groupChatId) {

        groupChatService.deleteGroupChat(groupChatId, jwtUtil.extractUserId(token.replace("Bearer ", "")));
        return "redirect:/all";
    }

    @PatchMapping("/{groupChatId}}/edit-descrip")
    public ResponseEntity<GroupChat> editGroupChatDescription(
            @RequestHeader("Authorization") String token,
            @PathVariable int groupChatId,
            @RequestParam String newDesc
    ) {
        groupChatService.editDescription(groupChatId, newDesc, jwtUtil.extractUserId(token.replace("Bearer ", "")));
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{groupChatId}/edit-name")
    public ResponseEntity<GroupChat> editGroupChatName(
            @RequestHeader("Authorization") String token,
            @PathVariable int groupChatId,
            @RequestParam String newName
    ) {
        groupChatService.editName(groupChatId, newName, jwtUtil.extractUserId(token.replace("Bearer ", "")));
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{groupChatId}/delete-user")
    public ResponseEntity<GroupChat> deleteUser(
            @RequestHeader("Authorization") String token,
            @PathVariable int groupChatId,
            @RequestParam int userId
    ) {
        groupChatService.deleteUser(groupChatId, userId, jwtUtil.extractUserId(token.replace("Bearer ", "")));
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{groupChatId}/add-user")
    public ResponseEntity<GroupChat> addUser(
            @PathVariable int groupChatId,
            @RequestParam int userId
    ) {
        groupChatService.addUser(groupChatId, userId, Roles.MEMBER);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{groupChatId}/change-role")
    public ResponseEntity<?> changeRole(@RequestHeader("Authorization") String token,
                                                       @PathVariable int groupChatId,
                                                       @RequestParam int memberId,
                                                       @RequestParam Roles role) {
        groupChatService.setRoleToMember(groupChatId, memberId, role, jwtUtil.extractUserId(token));
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{groupChatId}/members")
    public ResponseEntity<List<GroupChatMembers>> getGroupChatMembers(@PathVariable int groupChatId) {
        List<GroupChatMembers>  members =  groupChatService.getAllGroupChatMembersByGroupChat(groupChatId);
        return ResponseEntity.ok(members);
    }

    @DeleteMapping("/{groupChatId}/leave")
    public ResponseEntity<GroupChat> leaveGroupChatByUser(
            @PathVariable int groupChatId,
            @RequestParam int memberId
    ) {
        groupChatService.leaveGroupChat(groupChatId, memberId);
        return ResponseEntity.ok().build();
    }

}
