package com.utown.controller;

import com.utown.model.dto.ApiResponseDTO;
import com.utown.model.dto.user.ChangePasswordRequest;
import com.utown.model.dto.user.UpdateUserRequest;
import com.utown.model.dto.user.UserDTO;
import com.utown.service.UserService;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserService userService;

    @GetMapping("/api/user/me")
    @PreAuthorize("hasRole('CLIENT')")
    @Tag(name = "User (Client)", description = "Endpoints for clients")
    @Operation(
            summary = "Get current user profile",
            description = "Get authenticated user's profile information"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "User profile retrieved successfully",
                    content = @Content(schema = @Schema(implementation = UserDTO.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - invalid or missing token"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - requires CLIENT role"
            )
    })
    public ResponseEntity<ApiResponseDTO<UserDTO>> getCurrentUser(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        UserDTO user = userService.getCurrentUser(userId);

        return ResponseEntity.ok(ApiResponseDTO.success(user));
    }

    @PutMapping("/api/user/me")
    @PreAuthorize("hasRole('CLIENT')")
    @Tag(name = "User (Client)", description = "Endpoints for clients")
    @Operation(
            summary = "Update current user profile",
            description = "Update authenticated user's profile information"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Profile updated successfully",
                    content = @Content(schema = @Schema(implementation = UserDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - invalid or missing token"
            )
    })
    public ResponseEntity<ApiResponseDTO<UserDTO>> updateCurrentUser(
            Authentication authentication,
            @Valid @RequestBody UpdateUserRequest request
    ) {
        Long userId = (Long) authentication.getPrincipal();
        UserDTO user = userService.updateCurrentUser(userId, request);

        return ResponseEntity.ok(ApiResponseDTO.success(user, "Profile updated successfully"));
    }

    @PutMapping("/api/user/me/password")
    @PreAuthorize("hasRole('CLIENT')")
    @Tag(name = "User (Client)", description = "Endpoints for clients")
    @Operation(
            summary = "Change user password",
            description = "Change authenticated user's password"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Password changed successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid password or old password mismatch"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - invalid or missing token"
            )
    })
    public ResponseEntity<ApiResponseDTO<Void>> changePassword(
            Authentication authentication,
            @Valid @RequestBody ChangePasswordRequest request
    ) {
        Long userId = (Long) authentication.getPrincipal();
        userService.changePassword(userId, request);

        return ResponseEntity.ok(ApiResponseDTO.success("Password changed successfully"));
    }

    @GetMapping("/api/users")
    @PreAuthorize("hasRole('ADMIN')")
    @Tag(name = "Users (Admin)", description = "Admin endpoints for user management")
    @Operation(
            summary = "Get all users (Admin only)",
            description = "Retrieve paginated list of all users. Requires ADMIN role."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Users retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - invalid or missing token"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - requires ADMIN role"
            )
    })
    public ResponseEntity<ApiResponseDTO<Page<UserDTO>>> getAllUsers(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        Page<UserDTO> users = userService.getAllUsers(pageable);

        return ResponseEntity.ok(ApiResponseDTO.success(users));
    }

    @GetMapping("/api/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Tag(name = "Users (Admin)", description = "Admin endpoints for user management")
    @Operation(
            summary = "Get user by ID (Admin only)",
            description = "Retrieve user details by ID. Requires ADMIN role."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "User retrieved successfully",
                    content = @Content(schema = @Schema(implementation = UserDTO.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - invalid or missing token"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - requires ADMIN role"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found"
            )
    })
    public ResponseEntity<ApiResponseDTO<UserDTO>> getUserById(@PathVariable Long id) {
        UserDTO user = userService.getUserById(id);

        return ResponseEntity.ok(ApiResponseDTO.success(user));
    }

    @DeleteMapping("/api/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Tag(name = "Users (Admin)", description = "Admin endpoints for user management")
    @Operation(
            summary = "Delete user (Admin only)",
            description = "Delete user by ID. Requires ADMIN role."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "User deleted successfully"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - invalid or missing token"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - requires ADMIN role"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found"
            )
    })
    public ResponseEntity<ApiResponseDTO<Void>> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);

        return ResponseEntity.ok(ApiResponseDTO.success("User deleted successfully"));
    }
}
