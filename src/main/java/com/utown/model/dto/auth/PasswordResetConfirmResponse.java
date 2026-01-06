package com.utown.model.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PasswordResetConfirmResponse {
    private String message;

    public static PasswordResetConfirmResponse of(String message) {
        return PasswordResetConfirmResponse.builder()
                .message(message)
                .build();
    }
}
