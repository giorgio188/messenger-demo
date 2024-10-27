package com.project.messenger.dto;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.project.messenger.models.GroupChatFiles;
import com.project.messenger.models.GroupChatMembers;
import com.project.messenger.models.GroupChatMessages;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class GroupChatDTO {

    private String name;

    private String description;

    private Integer creatorId;


}
