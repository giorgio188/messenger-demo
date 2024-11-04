package com.project.messenger.controllers;

import com.project.messenger.models.PrivateChat;
import com.project.messenger.security.JWTUtil;
import com.project.messenger.services.privateChat.PrivateChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/private-chat")
@CrossOrigin(origins = "http://localhost:3000")
public class PrivateChatController {

    private final PrivateChatService privateChatService;
    private final JWTUtil jwtUtil;

    // Получение чата по ID
    @GetMapping("/{privateChatId}")
    public ResponseEntity<PrivateChat> getPrivateChat(
            @RequestHeader("Authorization") String token,
            @PathVariable int privateChatId) throws AccessDeniedException {
        int senderId = jwtUtil.extractUserId(token.replace("Bearer ", ""));
        PrivateChat privateChat = privateChatService.getPrivateChat(privateChatId, senderId);
        return ResponseEntity.ok(privateChat);
    }

    // Получение чата по сендеру и ресиверу (грубо говоря поиск чата)
    @GetMapping("/find")
    public ResponseEntity<PrivateChat> getPrivateChatBySenderAndReceiver(
            @RequestHeader("Authorization") String token,
            @RequestParam int receiverId) {
        int senderId = jwtUtil.extractUserId(token.replace("Bearer ", ""));
        PrivateChat privateChat = privateChatService.getPrivateChatBySenderAndReceiver(senderId, receiverId);
        return ResponseEntity.ok(privateChat);
    }

//    вывод всех чатов юзера
    @GetMapping()
    public ResponseEntity<List<PrivateChat>> getPrivateChatsOfUser(@RequestHeader("Authorization") String token) {
        int userId = jwtUtil.extractUserId(token.replace("Bearer ", ""));
        List<PrivateChat> chats = privateChatService.getAllChatsOfOneUser(userId);
        return ResponseEntity.ok(chats);
    }


    @PostMapping("/create")
    public ResponseEntity<PrivateChat> createPrivateChat(@RequestHeader("Authorization") String token, @RequestParam int receiverId) {
        int senderId = jwtUtil.extractUserId(token.replace("Bearer ", ""));
        privateChatService.createPrivateChat(senderId, receiverId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{privateChatId}")
    @ResponseStatus(HttpStatus.FOUND)
    public String deletePrivateChat(@PathVariable int privateChatId) {
        privateChatService.deletePrivateChat(privateChatId);
        return "redirect:/all";
    }
}
