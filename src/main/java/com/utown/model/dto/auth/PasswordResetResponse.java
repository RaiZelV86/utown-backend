package com.utown.model.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PasswordResetResponse {
    private String message;
    private Integer expiresInSeconds;

    public static PasswordResetResponse of(String message, Integer expiresInSeconds) {
        return PasswordResetResponse.builder()
                .message(message)
                .expiresInSeconds(expiresInSeconds)
                .build();
    }
}
