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
@RequestMapping("api/chat")
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

        // Отправляем сообщение
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


    @PatchMapping()
    public ResponseEntity<PrivateChatMessage> editPrivateMessage(
            @RequestParam int messageId,
            @RequestParam String editedMessage) {
        PrivateChatMessage updatedMessage = privateChatMessageService.editPrivateMessage(messageId, editedMessage);
        return ResponseEntity.ok(updatedMessage);
    }

    @PatchMapping("/{messageId}/read")
    public ResponseEntity<PrivateChatMessage> markMessageAsRead(@PathVariable int messageId) {
        PrivateChatMessage updatedMessage = privateChatMessageService.markMessageAsRead(messageId);
        return ResponseEntity.ok(updatedMessage);
    }
//
//    @MessageMapping("/private-chat/read")
//    public void markMessageAsReadWebSocket(@Payload int messageId) {
//        PrivateChatMessage updatedMessage = privateChatMessageService.markMessageAsRead(messageId);
//        messagingTemplate.convertAndSendToUser(String.valueOf(updatedMessage.getReceiver().getId()), "/queue/private-chat", updatedMessage);
//    }
    //    @MessageMapping("/private-chat/edit")
//    public void editMessageWebSocket(@Payload PrivateChatMessage message) {
//        PrivateChatMessage updatedMessage = privateChatMessageService.editPrivateMessage(
//                message.getId(),
//                message.getMessage());
//        messagingTemplate.convertAndSendToUser(String.valueOf(message.getReceiver().getId()), "/queue/private-chat", updatedMessage);
//    }

//    @MessageMapping("/private-chat/delete")
//    public void deleteMessageWebSocket(@Payload int messageId) {
//        privateChatMessageService.deletePrivateMessage(messageId);
//        messagingTemplate.convertAndSendToUser(String.valueOf(messageId), "/queue/private-chat", "Message deleted");
//    }


//    @PostMapping("/{privateChatId}")
//    public ResponseEntity<PrivateChatMessage> sendMessage(
//            @RequestHeader("Authorization") String token,
//            @RequestParam int receiverId,
//            @RequestParam String message) {
//        int senderId = jwtUtil.extractUserId(token.replace("Bearer ", ""));
//        PrivateChatMessage sentMessage = privateChatMessageService.sendMessage(senderId, receiverId, message);
//        return ResponseEntity.ok(sentMessage);
//    }

//    @MessageMapping("/{privateChatId}")
//    public void sendMessageWebSocket(@Payload PrivateChatMessage message) {
//        PrivateChatMessage sentMessage = privateChatMessageService.sendMessage(
//                message.getSender().getId(),
//                message.getReceiver().getId(),
//                message.getMessage());
//        messagingTemplate.convertAndSendToUser(String.valueOf(message.getReceiver().getId()), "/queue/private-chat", sentMessage);
//    }
}
