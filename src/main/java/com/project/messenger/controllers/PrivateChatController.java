package com.project.messenger.controllers;

import com.project.messenger.models.PrivateChat;
import com.project.messenger.services.PrivateChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/private-chat")
public class PrivateChatController {

    private final PrivateChatService privateChatService;

    @GetMapping("/{senderId}/{receiverId}")
    public ResponseEntity<PrivateChat> getPrivateChat(@PathVariable int senderId, @PathVariable int receiverId) {
        PrivateChat privateChat = privateChatService.getPrivateChat(senderId, receiverId);
        return ResponseEntity.ok(privateChat);
    }

    @GetMapping("/chats/{userId}")
    public ResponseEntity<List<PrivateChat>> getPrivateChatsOfUsers(@PathVariable int userId) {
        List<PrivateChat> chats = privateChatService.getAllChatsOfOneUser(userId);
        return ResponseEntity.ok(chats);
    }

    @PostMapping("/{senderId}/{receiverId}")
    public ResponseEntity<PrivateChat> createPrivateChat(@PathVariable int senderId, @PathVariable int receiverId) {
        privateChatService.createPrivateChat(senderId, receiverId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{privateChatId}")
    public RedirectView deletePrivateChat(@PathVariable int privateChatId, RedirectAttributes attributes) {
        privateChatService.deletePrivateChat(privateChatId);
        attributes.addFlashAttribute("message", "Chat deleted successfully");
        return new RedirectView("/private-chats/user/{userId}", true);
    }
}
