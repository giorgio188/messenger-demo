package com.project.messenger.controllers;

import com.project.messenger.dto.PrivateChatDTO;
import com.project.messenger.dto.UserUtilDTO;
import com.project.messenger.security.JWTUtil;
import com.project.messenger.services.PrivateChatMessageService;
import com.project.messenger.services.PrivateChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/private-chat")
@CrossOrigin(origins = "http://localhost:3000")
public class PrivateChatController {

    private final PrivateChatService privateChatService;
    private final JWTUtil jwtUtil;
    private final PrivateChatMessageService privateChatMessageService;

    @GetMapping("/{privateChatId}")
    public ResponseEntity<PrivateChatDTO> getPrivateChat(
            @RequestHeader("Authorization") String token,
            @PathVariable int privateChatId) throws AccessDeniedException {
        int senderId = jwtUtil.extractUserId(token.replace("Bearer ", ""));
        PrivateChatDTO privateChat = privateChatService.getPrivateChat(privateChatId, senderId);
        return ResponseEntity.ok(privateChat);
    }

    @GetMapping("/find/{receiverId}")
    public ResponseEntity<PrivateChatDTO> getPrivateChatBySenderAndReceiver(
            @RequestHeader("Authorization") String token,
            @PathVariable int receiverId) {
        try {
            int senderId = jwtUtil.extractUserId(token.replace("Bearer ", ""));
            PrivateChatDTO privateChat = privateChatService.getPrivateChatBySenderAndReceiver(senderId, receiverId);
            if (privateChat == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            return ResponseEntity.ok(privateChat);
        } catch (Exception e) {
            // Логируем ошибку для дальнейшего анализа
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping()
    public ResponseEntity<List<PrivateChatDTO>> getPrivateChatsOfUser(@RequestHeader("Authorization") String token) {
        int userId = jwtUtil.extractUserId(token.replace("Bearer ", ""));
        List<PrivateChatDTO> chats = privateChatService.getAllChatsOfOneUser(userId);
        return ResponseEntity.ok(chats);
    }

    @GetMapping("/{privateChatId}/members")
    public ResponseEntity<List<UserUtilDTO>> getPrivateChatMembers(@PathVariable int privateChatId) {
        List<UserUtilDTO> privateChatMembers = privateChatService.getPrivateChatParticipants(privateChatId);
        return ResponseEntity.ok(privateChatMembers);
    }

    @PostMapping("/create/{receiverId}")
    public ResponseEntity<PrivateChatDTO> createPrivateChat(@RequestHeader("Authorization") String token,
                                                            @PathVariable int receiverId) {
        int senderId = jwtUtil.extractUserId(token.replace("Bearer ", ""));
        privateChatService.createPrivateChat(senderId, receiverId);
        PrivateChatDTO privateChat = privateChatService.getPrivateChatBySenderAndReceiver(senderId, receiverId);
        return ResponseEntity.ok(privateChat);
    }

    @DeleteMapping("/{privateChatId}")
    public void deletePrivateChat(@PathVariable int privateChatId) {
        privateChatService.deletePrivateChat(privateChatId);
    }

    // WebSocket endpoint для входа пользователя в чат
    @MessageMapping("/private.enter")
    public void handleChatEnter(@Payload Map<String, Object> payload,
                                SimpMessageHeaderAccessor headerAccessor) {
        String token = headerAccessor.getFirstNativeHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            int userId = jwtUtil.extractUserId(token.replace("Bearer ", ""));
            int privateChatId = (Integer) payload.get("privateChatId");
            try {
                privateChatMessageService.markMessagesAsRead(privateChatId, userId);
            } catch (Exception e) {
                throw new RuntimeException("Failed to mark messages as read: " + e.getMessage());
            }
        }
    }
}
