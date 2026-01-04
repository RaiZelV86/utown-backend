package com.utown.controller;

import com.utown.model.dto.ApiResponse;
import com.utown.model.dto.auth.AuthResponse;
import com.utown.model.dto.auth.LoginRequest;
import com.utown.model.dto.auth.PasswordResetConfirmDto;
import com.utown.model.dto.auth.PasswordResetRequestDto;
import com.utown.model.dto.auth.PasswordResetVerifyDto;
import com.utown.model.dto.auth.RefreshTokenRequest;
import com.utown.model.dto.auth.RegisterRequest;
import com.utown.service.AuthService;
import com.utown.service.PasswordResetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@Slf4j
public class AuthController {

    private final AuthService authService;
    private final PasswordResetService passwordResetService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @Valid @RequestBody RegisterRequest request
    ) {
        log.info("POST /api/auth/register - phoneNumber: {}", request.getPhoneNumber());

        AuthResponse response = authService.register(request);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request
    ) {
        log.info("POST /api/auth/login - phoneNumber: {}", request.getPhoneNumber());

        AuthResponse response = authService.login(request);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(
            @Valid @RequestBody RefreshTokenRequest request
    ) {
        log.info("POST /api/auth/refresh");

        AuthResponse response = authService.refresh(request.getRefreshToken());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(Authentication authentication) {
        log.info("POST /api/auth/logout");

        Long userId = (Long) authentication.getPrincipal();

        authService.logout(userId);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/password/reset/request")
    public ResponseEntity<ApiResponse<Map<String, Object>>> requestPasswordReset(
            @Valid @RequestBody PasswordResetRequestDto request
    ) {
        log.info("POST /api/auth/password/reset/request - username: {}", request.getUsername());

        Map<String, Object> response = passwordResetService.requestPasswordReset(request);

        return ResponseEntity.ok(ApiResponse.success(response, "Reset code sent"));
    }

    @PostMapping("/password/reset/verify")
    public ResponseEntity<ApiResponse<Map<String, Object>>> verifyResetCode(
            @Valid @RequestBody PasswordResetVerifyDto request
    ) {
        log.info("POST /api/auth/password/reset/verify - username: {}", request.getUsername());

        Map<String, Object> response = passwordResetService.verifyResetCode(request);

        return ResponseEntity.ok(ApiResponse.success(response, "Code verified"));
    }

    @PostMapping("/password/reset/confirm")
    public ResponseEntity<ApiResponse<Map<String, Object>>> confirmPasswordReset(
            @Valid @RequestBody PasswordResetConfirmDto request
    ) {
        log.info("POST /api/auth/password/reset/confirm");

        Map<String, Object> response = passwordResetService.confirmPasswordReset(request);

        return ResponseEntity.ok(ApiResponse.success(response, "Password updated successfully"));
    }

}
