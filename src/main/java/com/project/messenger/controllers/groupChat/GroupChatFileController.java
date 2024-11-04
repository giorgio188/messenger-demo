package com.project.messenger.controllers.groupChat;

import com.project.messenger.models.GroupChat;
import com.project.messenger.models.GroupChatFiles;
import com.project.messenger.security.JWTUtil;
import com.project.messenger.services.groupChat.GroupChatFileService;
import com.project.messenger.services.groupChat.GroupChatMessageService;
import com.project.messenger.services.groupChat.GroupChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.List;

@RequestMapping("api/group-file")
@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class GroupChatFileController {

    private final GroupChatMessageService groupChatMessageService;
    private final GroupChatService groupChatService;
    private final JWTUtil jwtUtil;
    private final GroupChatFileService groupChatFileService;

    @PostMapping("/{groupChatId}")
    public ResponseEntity<GroupChatFiles> sendFile(
            @RequestHeader("Authorization") String token,
            @PathVariable int groupChatId,
            @RequestParam MultipartFile file) throws IOException {
        int memberId = jwtUtil.extractUserId(token.replace("Bearer ", ""));
        GroupChat groupChat = groupChatService.getGroupChat(groupChatId, memberId);
        if (groupChat == null) {
            throw new AccessDeniedException("Access denied");
        }
        GroupChatFiles groupChatFile = groupChatFileService.sendFile(memberId, groupChatId, file);
        return ResponseEntity.ok(groupChatFile);
    }

    @GetMapping("/{groupChatId}")
    public ResponseEntity<List<GroupChatFiles>> getGroupChatFiles(
            @PathVariable int groupChatId) {
        List<GroupChatFiles> files =groupChatFileService.getAllFiles(groupChatId);
        return ResponseEntity.ok(files);
    }

    @DeleteMapping("/{fileId}")
    public ResponseEntity<String> deleteMessage(
            @PathVariable int fileId) {
        groupChatFileService.deleteFile(fileId);
        return ResponseEntity.ok("File was deleted");
    }
}