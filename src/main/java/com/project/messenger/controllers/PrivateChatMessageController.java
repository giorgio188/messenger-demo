package com.project.messenger.controllers;

import com.project.messenger.models.PrivateChatMessage;
import com.project.messenger.services.PrivateChatMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/private-chat")
public class PrivateChatMessageController {

    private final PrivateChatMessageService privateChatMessageService;
    private final SimpMessagingTemplate messagingTemplate;


    @PostMapping()
    public ResponseEntity<PrivateChatMessage> sendMessage(
            @RequestParam int senderId,
            @RequestParam int receiverId,
            @RequestParam String message) {
        PrivateChatMessage sentMessage = privateChatMessageService.sendMessage(senderId, receiverId, message);
        return ResponseEntity.ok(sentMessage);
    }

    @MessageMapping("/private-chat/send")
    public void sendMessageWebSocket(@Payload PrivateChatMessage message) {
        PrivateChatMessage sentMessage = privateChatMessageService.sendMessage(
                message.getSender().getId(),
                message.getReceiver().getId(),
                message.getMessage());
        messagingTemplate.convertAndSendToUser(String.valueOf(message.getReceiver().getId()), "/queue/private-chat", sentMessage);
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

    @MessageMapping("/private-chat/delete")
    public void deleteMessageWebSocket(@Payload int messageId) {
        privateChatMessageService.deletePrivateMessage(messageId);
        messagingTemplate.convertAndSendToUser(String.valueOf(messageId), "/queue/private-chat", "Message deleted");
    }

    @PatchMapping("/{messageId}")
    public ResponseEntity<PrivateChatMessage> editPrivateMessage(
            @PathVariable int messageId,
            @RequestParam String editedMessage) {
        PrivateChatMessage updatedMessage = privateChatMessageService.editPrivateMessage(messageId, editedMessage);
        return ResponseEntity.ok(updatedMessage);
    }

    @MessageMapping("/private-chat/edit")
    public void editMessageWebSocket(@Payload PrivateChatMessage message) {
        PrivateChatMessage updatedMessage = privateChatMessageService.editPrivateMessage(
                message.getId(),
                message.getMessage());
        messagingTemplate.convertAndSendToUser(String.valueOf(message.getReceiver().getId()), "/queue/private-chat", updatedMessage);
    }

    @PatchMapping("/{messageId}/read")
    public ResponseEntity<PrivateChatMessage> markMessageAsRead(@PathVariable int messageId) {
        PrivateChatMessage updatedMessage = privateChatMessageService.markMessageAsRead(messageId);
        return ResponseEntity.ok(updatedMessage);
    }

    @MessageMapping("/private-chat/read")
    public void markMessageAsReadWebSocket(@Payload int messageId) {
        PrivateChatMessage updatedMessage = privateChatMessageService.markMessageAsRead(messageId);
        messagingTemplate.convertAndSendToUser(String.valueOf(updatedMessage.getReceiver().getId()), "/queue/private-chat", updatedMessage);
    }
}
