package com.project.messenger.dto;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.project.messenger.models.GroupChat;
import com.project.messenger.models.UserProfile;
import com.project.messenger.models.enums.MessageStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class GroupChatMessageDTO {

    private GroupChat groupChat;

    private UserProfile sender;

    private String message;

}
