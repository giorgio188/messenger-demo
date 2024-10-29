package com.project.messenger.controllers;

import com.project.messenger.models.PrivateChatMessage;
import com.project.messenger.models.UserProfile;
import com.project.messenger.models.enums.ProfileStatus;
import com.project.messenger.services.PrivateChatMessageService;
import com.project.messenger.services.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class WebSocketController {

    private final PrivateChatMessageService privateChatMessageService;
    private final UserProfileService userProfileService;
    private final SimpMessagingTemplate messagingTemplate;

    //    уведомление о прочтении сообщения
    @MessageMapping("/markAsRead")
    @SendTo("/queue/private-chat")
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
        List<UserProfile> friends = userProfileService.getFriendList(userId);
        for (UserProfile friend : friends) {
            messagingTemplate.convertAndSendToUser(String.valueOf(friend.getId()), "/queue/status", status);
        }
    }

    @MessageMapping("/chat.send/{chatId}")
    public void sendMessage(@Payload PrivateChatMessage message) {
        PrivateChatMessage sentMessage = privateChatMessageService.sendMessage(
                message.getSender().getId(),
                message.getReceiver().getId(),
                message.getMessage()
        );

        // Отправляем сообщение получателю
        messagingTemplate.convertAndSendToUser(
                String.valueOf(message.getReceiver().getId()),
                "/queue/private-chat",
                sentMessage
        );

        // Отправляем подтверждение отправителю
        messagingTemplate.convertAndSendToUser(
                String.valueOf(message.getSender().getId()),
                "/queue/message-status",
                Map.of("messageId", sentMessage.getId(), "status", "SENT")
        );
    }

    @MessageMapping("/chat.delete")
    public void deleteMessage(@Payload Map<String, Object> payload) {
        Integer messageId = (Integer) payload.get("messageId");
        Integer userId = (Integer) payload.get("userId");

        privateChatMessageService.deletePrivateMessage(messageId);

        // Уведомляем обоих участников чата об удалении
        messagingTemplate.convertAndSendToUser(
                String.valueOf(userId),
                "/queue/private-chat",
                Map.of("messageId", messageId, "action", "DELETED")
        );
    }

    @MessageMapping("/chat.edit")
    public void editMessage(@Payload PrivateChatMessage message) {
        PrivateChatMessage updatedMessage = privateChatMessageService.editPrivateMessage(
                message.getId(),
                message.getMessage()
        );

        // Отправляем обновленное сообщение всем участникам чата
        messagingTemplate.convertAndSendToUser(
                String.valueOf(message.getReceiver().getId()),
                "/queue/private-chat",
                updatedMessage
        );
    }

    @MessageMapping("/chat.read")
    public void markAsRead(@Payload Map<String, Object> payload) {
        Integer messageId = (Integer) payload.get("messageId");
        PrivateChatMessage updatedMessage = privateChatMessageService.markMessageAsRead(messageId);

        // Уведомляем отправителя о прочтении
        messagingTemplate.convertAndSendToUser(
                String.valueOf(updatedMessage.getSender().getId()),
                "/queue/message-status",
                Map.of("messageId", messageId, "status", "READ")
        );
    }
}
