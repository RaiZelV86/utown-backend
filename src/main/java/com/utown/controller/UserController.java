package com.utown.controller;

import com.utown.model.dto.ApiResponseDTO;
import com.utown.model.dto.user.ChangePasswordRequest;
import com.utown.model.dto.user.UpdateUserRequest;
import com.utown.model.dto.user.UserDTO;
import com.utown.service.UserService;
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
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<ApiResponseDTO<UserDTO>> getCurrentUser(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        UserDTO user = userService.getCurrentUser(userId);

        return ResponseEntity.ok(ApiResponseDTO.success(user));
    }

    @PutMapping("/me")
    public ResponseEntity<ApiResponseDTO<UserDTO>> updateCurrentUser(
            Authentication authentication,
            @Valid @RequestBody UpdateUserRequest request
    ) {
        Long userId = (Long) authentication.getPrincipal();
        UserDTO user = userService.updateCurrentUser(userId, request);

        return ResponseEntity.ok(ApiResponseDTO.success(user, "Profile updated successfully"));
    }

    @PutMapping("/me/password")
    public ResponseEntity<ApiResponseDTO<Void>> changePassword(
            Authentication authentication,
            @Valid @RequestBody ChangePasswordRequest request
    ) {
        Long userId = (Long) authentication.getPrincipal();
        userService.changePassword(userId, request);

        return ResponseEntity.ok(ApiResponseDTO.success("Password changed successfully"));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDTO<Page<UserDTO>>> getAllUsers(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        Page<UserDTO> users = userService.getAllUsers(pageable);

        return ResponseEntity.ok(ApiResponseDTO.success(users));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDTO<UserDTO>> getUserById(@PathVariable Long id) {
        UserDTO user = userService.getUserById(id);

        return ResponseEntity.ok(ApiResponseDTO.success(user));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDTO<Void>> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);

        return ResponseEntity.ok(ApiResponseDTO.success("User deleted successfully"));
    }
}
