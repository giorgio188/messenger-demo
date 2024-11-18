package com.project.messenger.controllers;

import com.project.messenger.models.UserProfile;
import com.project.messenger.security.JWTUtil;
import com.project.messenger.services.FriendListService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/friends")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class FriendListController {

    private final FriendListService friendListService;
    private final JWTUtil jwtUtil;

    @GetMapping("/{userId}")
    public ResponseEntity<List<UserProfile>> getFriendList(@PathVariable int userId) {
        List<UserProfile> friendList = friendListService.getFriendList(userId);
        return ResponseEntity.ok(friendList);
    }

    // WebSocket endpoint для добавления друга
    @MessageMapping("/friend.add")
    public void handleAddFriend(@Payload Map<String, Object> payload,
                                SimpMessageHeaderAccessor headerAccessor) {
        String token = headerAccessor.getFirstNativeHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            int userId = jwtUtil.extractUserId(token.replace("Bearer ", ""));
            int friendId = (Integer) payload.get("friendId");
            try {
                friendListService.addFriend(userId, friendId);
            } catch (Exception e) {
                throw new RuntimeException("Failed to add friend: " + e.getMessage());
            }
        }
    }

    // WebSocket endpoint для удаления друга
    @MessageMapping("/friend.delete")
    public void handleDeleteFriend(@Payload Map<String, Object> payload,
                                   SimpMessageHeaderAccessor headerAccessor) {
        String token = headerAccessor.getFirstNativeHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            int userId = jwtUtil.extractUserId(token.replace("Bearer ", ""));
            int friendId = (Integer) payload.get("friendId");
            try {
                friendListService.deleteFriend(userId, friendId);
            } catch (Exception e) {
                throw new RuntimeException("Failed to delete friend: " + e.getMessage());
            }
        }
    }

}

//    @PostMapping("/addFriend")
//    @ResponseStatus(HttpStatus.FOUND)
//    public ResponseEntity<?> addFriend (@RequestHeader("Authorization") String token,
//                             @RequestParam int friendId) {
//        int userId = jwtUtil.extractUserId(token.replace("Bearer ", ""));
//        friendListService.addFriend(userId, friendId);
//        return new ResponseEntity<>(HttpStatus.OK);
//    }
//
//    @DeleteMapping("/deleteFriend")
//    @ResponseStatus(HttpStatus.FOUND)
//    public ResponseEntity<?> deleteFriend (@RequestHeader("Authorization") String token,
//                                @RequestParam int friendId) {
//        int userId = jwtUtil.extractUserId(token.replace("Bearer ", ""));
//        friendListService.deleteFriend(userId, friendId);
//        return new ResponseEntity<>(HttpStatus.OK);
//    }
