package com.project.messenger.controllers;

import com.project.messenger.models.GroupChat;
import com.project.messenger.models.GroupChatMessage;
import com.project.messenger.security.JWTUtil;
import com.project.messenger.services.GroupChatMessageService;
import com.project.messenger.services.GroupChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.List;

@RequestMapping("api/group-messege")
@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class GroupChatMessageController {

    private final GroupChatMessageService groupChatMessageService;
    private final GroupChatService groupChatService;
    private final JWTUtil jwtUtil;

    @PostMapping("/{groupChatId}")
    public ResponseEntity<GroupChatMessage> sendMessage(
            @RequestHeader("Authorization") String token,
            @PathVariable int groupChatId,
            @RequestParam String message) throws AccessDeniedException {
        int memberId = jwtUtil.extractUserId(token.replace("Bearer ", ""));
        GroupChat groupChat = groupChatService.getGroupChat(groupChatId, memberId);
        if (groupChat == null) {
            throw new AccessDeniedException("Access denied");
        }
        GroupChatMessage groupChatMessage = groupChatMessageService.sendMessage(memberId, groupChatId, message);
        return ResponseEntity.ok(groupChatMessage);
    }

    @GetMapping("/{groupChatId}")
    public ResponseEntity<List<GroupChatMessage>> getGroupChatMessages(
            @PathVariable int groupChatId
    ) {
        List<GroupChatMessage> messages = groupChatMessageService.getGroupChatMessages(groupChatId);
        return ResponseEntity.ok(messages);
    }

    @DeleteMapping("/{MessageId}")
    public ResponseEntity<String> deleteMessage(
            @PathVariable int MessageId
    ) {
        groupChatMessageService.deleteGroupMessage(MessageId);
        return ResponseEntity.ok("Message deleted");
    }

    @PatchMapping()
    public ResponseEntity<GroupChatMessage> editMessage(
            @RequestParam int messageId,
            @RequestParam String editedTextMessage
    ) {
        GroupChatMessage updatedMessage = groupChatMessageService.editGroupMessage(messageId, editedTextMessage);
        return ResponseEntity.ok(updatedMessage);
    }



}
