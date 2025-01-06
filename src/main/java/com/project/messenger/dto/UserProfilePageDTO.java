package com.project.messenger.dto;

import lombok.Data;

@Data
public class UserProfilePageDTO {
    private String username;
    private String nickname;
    private String phoneNumber;
    private String email;
    private String avatar;
}
