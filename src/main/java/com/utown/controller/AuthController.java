package com.utown.controller;

import com.utown.model.dto.ApiResponseDTO;
import com.utown.model.dto.auth.AuthResponse;
import com.utown.model.dto.auth.LoginRequest;
import com.utown.model.dto.auth.PasswordResetConfirmDto;
import com.utown.model.dto.auth.PasswordResetRequestDto;
import com.utown.model.dto.auth.PasswordResetVerifyDto;
import com.utown.model.dto.auth.RefreshTokenRequest;
import com.utown.model.dto.auth.RegisterRequest;
import com.utown.service.AuthService;
import com.utown.service.PasswordResetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Authentication", description = "Authentication and authorization endpoints")
public class AuthController {

    private final AuthService authService;
    private final PasswordResetService passwordResetService;

    @PostMapping("/register")
    @Operation(
            summary = "Register new user",
            description = "Register a new user account with phone number and password"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "User registered successfully",
                    content = @Content(schema = @Schema(implementation = AuthResponse.class))
            ),

            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input or user already exists"
            )
    })
    public ResponseEntity<AuthResponse> register(
            @Valid @RequestBody RegisterRequest request
    ) {
        log.info("POST /api/auth/register - phoneNumber: {}", request.getPhoneNumber());

        AuthResponse response = authService.register(request);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    @Operation(
            summary = "User login",
            description = "Authenticate user and receive access and refresh tokens"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Login successful",
                    content = @Content(schema = @Schema(implementation = AuthResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Invalid credentials"
            )
    })
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request
    ) {
        log.info("POST /api/auth/login - phoneNumber: {}", request.getPhoneNumber());

        AuthResponse response = authService.login(request);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    @Operation(
            summary = "Refresh access token",
            description = "Get new access token using refresh token"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Token refreshed successfully",
                    content = @Content(schema = @Schema(implementation = AuthResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Invalid or expired refresh token"
            )
    })
    public ResponseEntity<AuthResponse> refresh(
            @Valid @RequestBody RefreshTokenRequest request
    ) {
        log.info("POST /api/auth/refresh");

        AuthResponse response = authService.refresh(request.getRefreshToken());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "User logout",
            description = "Invalidate all refresh tokens for the authenticated user"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Logout successful"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - invalid or missing token"
            )
    })
    public ResponseEntity<Void> logout(Authentication authentication) {
        log.info("POST /api/auth/logout");

        Long userId = (Long) authentication.getPrincipal();

        authService.logout(userId);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/password/reset/request")
    public ResponseEntity<ApiResponseDTO<Map<String, Object>>> requestPasswordReset(
            @Valid @RequestBody PasswordResetRequestDto request
    ) {
        log.info("POST /api/auth/password/reset/request - username: {}", request.getUsername());

        Map<String, Object> response = passwordResetService.requestPasswordReset(request);

        return ResponseEntity.ok(ApiResponseDTO.success(response, "Reset code sent"));
    }

    @PostMapping("/password/reset/verify")
    public ResponseEntity<ApiResponseDTO<Map<String, Object>>> verifyResetCode(
            @Valid @RequestBody PasswordResetVerifyDto request
    ) {
        log.info("POST /api/auth/password/reset/verify - username: {}", request.getUsername());

        Map<String, Object> response = passwordResetService.verifyResetCode(request);

        return ResponseEntity.ok(ApiResponseDTO.success(response, "Code verified"));
    }

    @PostMapping("/password/reset/confirm")
    public ResponseEntity<ApiResponseDTO<Map<String, Object>>> confirmPasswordReset(
            @Valid @RequestBody PasswordResetConfirmDto request
    ) {
        log.info("POST /api/auth/password/reset/confirm");

        Map<String, Object> response = passwordResetService.confirmPasswordReset(request);

        return ResponseEntity.ok(ApiResponseDTO.success(response, "Password updated successfully"));
    }

}
