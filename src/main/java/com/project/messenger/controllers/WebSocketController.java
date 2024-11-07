package com.project.messenger.controllers;

import com.project.messenger.dto.UserProfileDTO;
import com.project.messenger.models.UserProfile;
import com.project.messenger.models.enums.ProfileStatus;
import com.project.messenger.models.enums.Roles;
import com.project.messenger.security.JWTUtil;
import com.project.messenger.services.groupChat.GroupChatMessageService;
import com.project.messenger.services.groupChat.GroupChatService;
import com.project.messenger.services.privateChat.PrivateChatMessageService;
import com.project.messenger.services.privateChat.PrivateChatService;
import com.project.messenger.services.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.stereotype.Controller;

import java.nio.file.AccessDeniedException;


@Controller
@RequiredArgsConstructor
public class WebSocketController {

    private final PrivateChatMessageService chatMessageService;
    private final PrivateChatService privateChatService;
    private final UserProfileService userProfileService;
    private final JWTUtil jwtUtil;
    private final ModelMapper modelMapper;
    private final GroupChatService groupChatService;
    private final GroupChatMessageService groupChatMessageService;

    // Приватные чаты
    @MessageMapping("/private.chat.create/{receiverId}")
    public void createPrivateChat(@DestinationVariable int receiverId,
                                  @Header("Authorization") String token) {
        int userId = jwtUtil.extractUserId(token.replace("Bearer ", ""));
        privateChatService.createPrivateChat(userId, receiverId);
    }

    @MessageMapping("/private.chat.find/{receiverId}")
    public void findPrivateChat(@DestinationVariable int receiverId,
                                @Header("Authorization") String token) throws AccessDeniedException {
        int userId = jwtUtil.extractUserId(token.replace("Bearer ", ""));
        privateChatService.getPrivateChatBySenderAndReceiver(userId, receiverId);
    }

    // Приватные сообщения
    @MessageMapping("/private.message.send/{chatId}")
    public void sendPrivateMessage(@DestinationVariable int chatId,
                                   @Payload String message,
                                   @Header("Authorization") String token) throws AccessDeniedException {
        int senderId = jwtUtil.extractUserId(token.replace("Bearer ", ""));
        privateChatService.getPrivateChat(chatId, senderId);
        chatMessageService.sendMessage(senderId, chatId, message);
    }

    @MessageMapping("/private.message.edit/{messageId}")
    public void editPrivateMessage(@DestinationVariable int messageId,
                                   @Payload String message,
                                   @Header("Authorization") String token) {
        chatMessageService.editPrivateMessage(messageId, message);
    }

    @MessageMapping("/private.message.delete/{messageId}")
    public void deletePrivateMessage(@DestinationVariable int messageId,
                                     @Header("Authorization") String token) {
        chatMessageService.deletePrivateMessage(messageId);
    }

    @MessageMapping("/private.message.read/{messageId}")
    public void markPrivateMessageAsRead(@DestinationVariable int messageId) {
        chatMessageService.markMessageAsRead(messageId);
    }

    // Пользователи
    @MessageMapping("/user.status")
    public void updateUserStatus(@Payload ProfileStatus status,
                                 @Header("Authorization") String token) {
        int userId = jwtUtil.extractUserId(token.replace("Bearer ", ""));
        userProfileService.setUserOnlineStatus(userId, status);
    }

    @MessageMapping("/user.update")
    public void updateUserProfile(@Payload UserProfileDTO userProfileDTO,
                                  @Header("Authorization") String token) {
        int userId = jwtUtil.extractUserId(token.replace("Bearer ", ""));
        userProfileService.updateUserProfile(
                userId,
                modelMapper.map(userProfileDTO, UserProfile.class)
        );
    }

    // Друзья
    @MessageMapping("/friend.add/{friendId}")
    public void addFriend(@DestinationVariable int friendId,
                          @Header("Authorization") String token) {
        int userId = jwtUtil.extractUserId(token.replace("Bearer ", ""));
        userProfileService.addFriend(userId, friendId);
    }

    @MessageMapping("/friend.delete/{friendId}")
    public void deleteFriend(@DestinationVariable int friendId,
                             @Header("Authorization") String token) {
        int userId = jwtUtil.extractUserId(token.replace("Bearer ", ""));
        userProfileService.deleteFriend(userId, friendId);
    }

    // Групповые чаты
    @MessageMapping("/group.chat.create")
    public void createGroupChat(@Payload String groupName,
                                @Header("Authorization") String token) {
        int creatorId = jwtUtil.extractUserId(token.replace("Bearer ", ""));
        groupChatService.createGroupChat(groupName, "", creatorId);
    }

    @MessageMapping("/group.chat.find/{groupId}")
    public void findGroupChat(@DestinationVariable int groupId,
                              @Header("Authorization") String token) throws AccessDeniedException {
        int userId = jwtUtil.extractUserId(token.replace("Bearer ", ""));
        groupChatService.getGroupChat(groupId, userId);
    }

    // Групповые сообщения
    @MessageMapping("/group.message.send/{groupId}")
    public void sendGroupMessage(@DestinationVariable int groupId,
                                 @Payload String message,
                                 @Header("Authorization") String token) throws AccessDeniedException {
        int senderId = jwtUtil.extractUserId(token.replace("Bearer ", ""));
        groupChatService.getGroupChat(groupId, senderId);
        groupChatMessageService.sendMessage(senderId, groupId, message);
    }

    @MessageMapping("/group.message.edit/{messageId}")
    public void editGroupMessage(@DestinationVariable int messageId,
                                 @Payload String message,
                                 @Header("Authorization") String token) {
        groupChatMessageService.editGroupMessage(messageId, message);
    }

    @MessageMapping("/group.message.delete/{messageId}")
    public void deleteGroupMessage(@DestinationVariable int messageId,
                                   @Header("Authorization") String token) {
        groupChatMessageService.deleteGroupMessage(messageId);
    }

    // Управление участниками
    @MessageMapping("/group.member.add/{groupId}/{userId}")
    public void addGroupMember(@DestinationVariable int groupId,
                               @DestinationVariable int userId,
                               @Header("Authorization") String token) {
        // По умолчанию добавляем с ролью MEMBER
        groupChatService.addUser(groupId, userId, Roles.MEMBER);
    }

    @MessageMapping("/group.member.remove/{groupId}/{userId}")
    public void removeGroupMember(@DestinationVariable int groupId,
                                  @DestinationVariable int userId,
                                  @Header("Authorization") String token) {
        int adminId = jwtUtil.extractUserId(token.replace("Bearer ", ""));
        groupChatService.deleteUser(groupId, userId, adminId);
    }

    @MessageMapping("/group.member.leave/{groupId}")
    public void leaveGroupChat(@DestinationVariable int groupId,
                               @Header("Authorization") String token) {
        int userId = jwtUtil.extractUserId(token.replace("Bearer ", ""));
        groupChatService.leaveGroupChat(groupId, userId);
    }
}

//  private final PrivateChatMessageService privateChatMessageService;
//    private final UserProfileService userProfileService;
//    private final SimpMessagingTemplate messagingTemplate;
//
//    //    уведомление о прочтении сообщения
//    @MessageMapping("/markAsRead")
//    @SendTo("/queue/private-chat")
//    public void markMessageAsRead(@Payload int messageId) {
//        privateChatMessageService.markMessageAsRead(messageId);
//    }
//
//    //    уведомление, что юзер онлайн
//    @MessageMapping("/connect")
//    public void userConnected(@Payload int userId) {
//        userProfileService.setUserOnlineStatus(userId, ProfileStatus.ONLINE);
//        notifyFriends(userId, ProfileStatus.ONLINE);
//    }
//
//    //    уведомление, что юзер офлайн
//    @MessageMapping("/disconnect")
//    public void userDisconnected(@Payload int userId) {
//        userProfileService.setUserOnlineStatus(userId, ProfileStatus.OFFLINE);
//        notifyFriends(userId, ProfileStatus.OFFLINE);
//    }
//
//    //  уведомление друзьям, что юзер онлайн/офлайн
//    private void notifyFriends(int userId, ProfileStatus status) {
//        List<UserProfile> friends = userProfileService.getFriendList(userId);
//        for (UserProfile friend : friends) {
//            messagingTemplate.convertAndSendToUser(String.valueOf(friend.getId()), "/queue/status", status);
//        }
//    }
//
//    @MessageMapping("/chat.send/{chatId}")
//    public void sendMessage(@Payload PrivateChatMessage message) {
//        PrivateChatMessage sentMessage = privateChatMessageService.sendMessage(
//                message.getSender().getId(),
//                message.getReceiver().getId(),
//                message.getMessage()
//        );
//
//        // Отправляем сообщение получателю
//        messagingTemplate.convertAndSendToUser(
//                String.valueOf(message.getReceiver()),
//                "/queue/private-chat",
//                sentMessage
//        );
//
//        // Отправляем подтверждение отправителю
//        messagingTemplate.convertAndSendToUser(
//                String.valueOf(message.getSender()),
//                "/queue/message-status",
//                Map.of("messageId", sentMessage.getId(), "status", "SENT")
//        );
//    }
//
//    @MessageMapping("/chat.delete")
//    public void deleteMessage(@Payload Map<String, Object> payload) {
//        Integer messageId = (Integer) payload.get("messageId");
//        Integer userId = (Integer) payload.get("userId");
//
//        privateChatMessageService.deletePrivateMessage(messageId);
//
//        // Уведомляем обоих участников чата об удалении
//        messagingTemplate.convertAndSendToUser(
//                String.valueOf(userId),
//                "/queue/private-chat",
//                Map.of("messageId", messageId, "action", "DELETED")
//        );
//    }
//
//    @MessageMapping("/chat.edit")
//    public void editMessage(@Payload PrivateChatMessage message) {
//        PrivateChatMessage updatedMessage = privateChatMessageService.editPrivateMessage(
//                message.getId(),
//                message.getMessage()
//        );
//
//        // Отправляем обновленное сообщение всем участникам чата
//        messagingTemplate.convertAndSendToUser(
//                String.valueOf(message.getReceiver().getId()),
//                "/queue/private-chat",
//                updatedMessage
//        );
//    }
//
//    @MessageMapping("/chat.read")
//    public void markAsRead(@Payload Map<String, Object> payload) {
//        Integer messageId = (Integer) payload.get("messageId");
//        PrivateChatMessage updatedMessage = privateChatMessageService.markMessageAsRead(messageId);
//
//        // Уведомляем отправителя о прочтении
//        messagingTemplate.convertAndSendToUser(
//                String.valueOf(updatedMessage.getSender().getId()),
//                "/queue/message-status",
//                Map.of("messageId", messageId, "status", "READ")
//        );
//    }
