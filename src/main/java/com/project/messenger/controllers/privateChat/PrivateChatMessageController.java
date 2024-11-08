package com.project.messenger.controllers.privateChat;

import com.project.messenger.dto.PrivateChatDTO;
import com.project.messenger.dto.PrivateChatMessageDTO;
import com.project.messenger.models.PrivateChat;
import com.project.messenger.models.PrivateChatMessage;
import com.project.messenger.security.JWTUtil;
import com.project.messenger.services.privateChat.PrivateChatMessageService;
import com.project.messenger.services.privateChat.PrivateChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<PrivateChatMessageDTO> sendMessage(
            @RequestHeader("Authorization") String token,
            @PathVariable int privateChatId,
            @RequestParam String message) throws AccessDeniedException {
        int senderId = jwtUtil.extractUserId(token.replace("Bearer ", ""));
        PrivateChatDTO chat = privateChatService.getPrivateChat(privateChatId, senderId);
        if (chat == null) {
            throw new AccessDeniedException("У вас нет доступа к этому чату");
        }
        PrivateChatMessageDTO sentMessage = privateChatMessageService.sendMessage(senderId, privateChatId, message);
        return ResponseEntity.ok(sentMessage);
    }

    @GetMapping("/{privateChatId}")
    public ResponseEntity<List<PrivateChatMessageDTO>> getPrivateChatMessages(@PathVariable int privateChatId) {
        List<PrivateChatMessageDTO> messages = privateChatMessageService.getPrivateChatMessages(privateChatId);
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
}
