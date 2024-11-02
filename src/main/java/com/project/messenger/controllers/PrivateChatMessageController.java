package com.project.messenger.controllers;

import com.project.messenger.models.PrivateChat;
import com.project.messenger.models.PrivateChatMessage;
import com.project.messenger.security.JWTUtil;
import com.project.messenger.services.PrivateChatMessageService;
import com.project.messenger.services.PrivateChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/private-message")
@CrossOrigin(origins = "http://localhost:3000")
public class PrivateChatMessageController {

    private final PrivateChatMessageService privateChatMessageService;
    private final JWTUtil jwtUtil;
    private final PrivateChatService privateChatService;


    @PostMapping("/{privateChatId}")
    public ResponseEntity<PrivateChatMessage> sendMessage(
            @RequestHeader("Authorization") String token,
            @PathVariable int privateChatId,
            @RequestParam String message) throws AccessDeniedException {
        int senderId = jwtUtil.extractUserId(token.replace("Bearer ", ""));
        PrivateChat chat = privateChatService.getPrivateChat(privateChatId, senderId);
        if (chat == null) {
            throw new AccessDeniedException("У вас нет доступа к этому чату");
        }
        PrivateChatMessage sentMessage = privateChatMessageService.sendMessage(senderId, privateChatId, message);
        return ResponseEntity.ok(sentMessage);
    }

    @GetMapping("/{privateChatId}")
    public ResponseEntity<List<PrivateChatMessage>> getPrivateChatMessages(@PathVariable int privateChatId) {
        List<PrivateChatMessage> messages = privateChatMessageService.getPrivateChatMessages(privateChatId);
        return ResponseEntity.ok(messages);
    }

    @DeleteMapping("/{messageId}")
    public ResponseEntity<String> deletePrivateMessage(@PathVariable int messageId) {
        privateChatMessageService.deletePrivateMessage(messageId);
        return ResponseEntity.ok("Message deleted");
    }


    @PatchMapping("/{messageId}")
    public ResponseEntity<PrivateChatMessage> editPrivateMessage(
            @PathVariable int messageId,
            @RequestParam String editedMessage) {
        PrivateChatMessage updatedMessage = privateChatMessageService.editPrivateMessage(messageId, editedMessage);
        return ResponseEntity.ok(updatedMessage);
    }

    @PatchMapping("/{messageId}/read")
    public ResponseEntity<PrivateChatMessage> markMessageAsRead(@PathVariable int messageId) {
        PrivateChatMessage updatedMessage = privateChatMessageService.markMessageAsRead(messageId);
        return ResponseEntity.ok(updatedMessage);
    }

//    @PostMapping("/{privateChatId}")
//    public ResponseEntity<PrivateChatMessage> sendMessage(
//            @RequestHeader("Authorization") String token,
//            @RequestParam int receiverId,
//            @RequestParam String message) {
//        int senderId = jwtUtil.extractUserId(token.replace("Bearer ", ""));
//        PrivateChatMessage sentMessage = privateChatMessageService.sendMessage(senderId, receiverId, message);
//        return ResponseEntity.ok(sentMessage);
//    }

}
