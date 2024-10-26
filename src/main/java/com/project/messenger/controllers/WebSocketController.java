package com.project.messenger.controllers;

import com.project.messenger.models.UserProfile;
import com.project.messenger.models.enums.ProfileStatus;
import com.project.messenger.services.PrivateChatMessageService;
import com.project.messenger.services.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class WebSocketController {

    private final PrivateChatMessageService privateChatMessageService;
    private final UserProfileService userProfileService;
    private SimpMessagingTemplate messagingTemplate;

//    уведомление о прочтении сообщения
    @MessageMapping("/markAsRead")
    public void markMessageAsRead(@Payload int messageId) {
        privateChatMessageService.markMessageAsRead(messageId);
    }

//    уведомление, что юзер онлайн
    @MessageMapping("/connect")
    public void userConnected(@Payload int userId) {
        userProfileService.setUserOnlineStatus(userId, ProfileStatus.ONLINE);
        notifyFriends(userId, ProfileStatus.ONLINE);
    }
//    уведомление, что юзер офлайн
    @MessageMapping("/disconnect")
    public void userDisconnected(@Payload int userId) {
        userProfileService.setUserOnlineStatus(userId, ProfileStatus.OFFLINE);
        notifyFriends(userId, ProfileStatus.OFFLINE);
    }
//  уведомление друзьям, что юзер онлайн/офлайн
    private void notifyFriends(int userId, ProfileStatus status) {
        List<UserProfile> friends = userProfileService.getFriendLists(userId);
        for (UserProfile friend : friends) {
            messagingTemplate.convertAndSendToUser(String.valueOf(friend.getId()), "/queue/status", status);
        }
    }
}
