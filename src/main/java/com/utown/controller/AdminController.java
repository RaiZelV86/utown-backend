package com.utown.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@Tag(name = "Admin", description = "Endpoints for ADMIN")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @GetMapping("/dashboard")
    @Operation(summary = "Admin Dashboard", description = "Статистика для администратора")
    public ResponseEntity<Map<String, Object>> getDashboard() {
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Admin dashboard data",
                "stats", Map.of(
                        "totalUsers", 100,
                        "totalRestaurants", 50,
                        "totalOrders", 1000
                )
        ));
    }

    @GetMapping("/users")
    @Operation(summary = "Все пользователи", description = "Список всех пользователей")
    public ResponseEntity<Map<String, Object>> getAllUsers() {
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "All users"
        ));
    }
}
