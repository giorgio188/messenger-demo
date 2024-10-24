package com.project.messenger.dto;

import com.project.messenger.models.enums.ProfileStatus;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegistrationDTO {

    @Size(min = 8, max = 60)
    @NotNull
    private String username;

    @NotNull
    private String password;

    @NotNull
    @Size(min = 3, max = 60)
    private String nickname;

    @NotNull
    @Size(min = 10, max = 10)
    @Pattern(regexp = "^9\\d{9}$")
    private String phoneNumber;

    @Email
    @NotNull
    private String email;

}
