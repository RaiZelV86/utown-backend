package com.utown.model.dto.auth;

import com.utown.model.entity.PasswordResetRequest;
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

    public static PasswordResetRequest of(String message, Integer expiresInSeconds) {
        return PasswordResetRequest.builder()
                .message(message)
                .expiresInSeconds(expiresInSeconds)
                .build();
    }
}
