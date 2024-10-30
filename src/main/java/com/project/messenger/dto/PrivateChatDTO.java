package com.project.messenger.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PrivateChatDTO {
    private int id;
    private int senderId;
    private String senderUsername;
    private int receiverId;
    private String receiverUsername;
    private LocalDateTime createdAt;
}
