package com.project.messenger.utils;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class WebSocketAuthInterceptor implements ChannelInterceptor {

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            UsernamePasswordAuthenticationToken authentication =
                    (UsernamePasswordAuthenticationToken) accessor.getUser();
            if (authentication != null) {
                // Предполагаем, что у нас есть UserProfile в Principal
                String userId = authentication.getName();
                accessor.setSessionAttributes(Map.of("userId", userId));
            }
        }
        return message;
    }

}
