package com.utown.model.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PasswordResetVerifyResponse {
    private String resetToken;
    private Integer expiresInSeconds;

    public static PasswordResetVerifyResponse of(String resetToken, Integer expiresInSeconds) {
        return PasswordResetVerifyResponse.builder()
                .resetToken(resetToken)
                .expiresInSeconds(expiresInSeconds)
                .build();
    }
}
