package com.project.messenger.dto;

import lombok.Data;

@Data
public class GroupChatMembersDTO {
    private String memberId;
    private String username;
    private String nickname;
    private String role;
}
