package com.project.messenger.controllers.privateChat;

import com.project.messenger.models.PrivateChatFiles;
import com.project.messenger.security.JWTUtil;
import com.project.messenger.services.privateChat.PrivateChatFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/private-file")
@CrossOrigin(origins = "http://localhost:3000")
public class PrivateChatFileController {

    private final PrivateChatFileService privateChatFileService;
    private final JWTUtil jwtUtil;

    @GetMapping("/{privateChatId}")
    public ResponseEntity<List<PrivateChatFiles>> getPrivateChatFiles(@PathVariable int privateChatId) {
        List<PrivateChatFiles> privateChatFiles = privateChatFileService.getPrivateChatFiles(privateChatId);
        return ResponseEntity.ok(privateChatFiles);
    }

    @PostMapping("/{privateChatId}")
    public ResponseEntity<PrivateChatFiles> sendPrivateChatFile(@RequestHeader("Authorization") String token,
                                                                @PathVariable int privateChatId,
                                                                @RequestParam MultipartFile file) {
        int senderId = jwtUtil.extractUserId(token.replace("Bearer ", ""));
        PrivateChatFiles privateChatFile = privateChatFileService.sendPrivateChatFile(senderId, privateChatId, file);
        return ResponseEntity.ok(privateChatFile);
    }

    @DeleteMapping("/{fileId}")
    public ResponseEntity<String> deletePrivateChatFile(@PathVariable int fileId) {
        privateChatFileService.deletePrivateChatFile(fileId);
        return ResponseEntity.ok("File deleted");
    }

}
