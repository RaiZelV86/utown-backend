package com.utown.model.dto.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDTO {
    private Long id;
    private String phoneNumber;
    private String name;
    private String email;
    private String role;
    private String profileImageUrl;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime lastLoginAt;
}
