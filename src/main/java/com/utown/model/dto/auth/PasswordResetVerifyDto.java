package com.utown.model.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PasswordResetVerifyDto {

    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "Code is required")
    @Size(min = 4, max = 4, message = "Code must be 4 digits")
    private String code;
}
