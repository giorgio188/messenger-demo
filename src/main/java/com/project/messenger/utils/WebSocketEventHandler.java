package com.project.messenger.utils;

import com.project.messenger.models.enums.ProfileStatus;
import com.project.messenger.services.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
@RequiredArgsConstructor
public class WebSocketEventHandler {

    private final UserProfileService userProfileService;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String userId = headerAccessor.getSessionAttributes().get("userId").toString();
        if (userId != null) {
            userProfileService.setUserOnlineStatus(Integer.parseInt(userId), ProfileStatus.ONLINE);
        }
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String userId = headerAccessor.getSessionAttributes().get("userId").toString();
        if (userId != null) {
            userProfileService.setUserOnlineStatus(Integer.parseInt(userId), ProfileStatus.OFFLINE);
        }
    }

}
