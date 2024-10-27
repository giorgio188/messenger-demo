package com.project.messenger.controllers;

import com.project.messenger.dto.GroupChatMessageDTO;
import com.project.messenger.models.GroupChatMessages;
import com.project.messenger.models.UserProfile;
import com.project.messenger.services.GroupChatMessageService;
import com.project.messenger.services.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RequestMapping("/group-chat/messeges")
@RestController
@RequiredArgsConstructor
public class GroupChatMessageController {

    private final GroupChatMessageService groupChatMessageService;

    @PostMapping("/send")
    public Map<String, String> sendMessage(@RequestBody GroupChatMessageDTO groupChatMessagesDTO) {
        int senderId = groupChatMessagesDTO.getSender().getId();
        int groupChatId = groupChatMessagesDTO.getGroupChat().getId();
        String message = groupChatMessagesDTO.getMessage();

        groupChatMessageService.sendMessage(senderId, groupChatId, message);
        return Map.of("Message was sent", message);
    }

    @MessageMapping("/chat.sendMessage")
    @SendTo("/queue/group-chat")
    public GroupChatMessages sendMessageViaWebSocket(@Payload GroupChatMessageDTO groupChatMessagesDTO) {

        int senderId = groupChatMessagesDTO.getSender().getId();
        int groupChatId = groupChatMessagesDTO.getGroupChat().getId();
        String message = groupChatMessagesDTO.getMessage();

        return  groupChatMessageService.sendMessage(senderId, groupChatId, message);
    }
}
