package com.utown.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/public")
@Tag(name="Public", description = "Public endpoints without authorization")
public class PublicController {

    @GetMapping("/health")
    @Operation(summary = "Health check", description = "Проверка работоспособности API")
    public ResponseEntity<Map<String, String>> healthCheck() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "message", "UTOWN API is running"
        ));
    }

    @GetMapping("/info")
    @Operation(summary = "API Information", description = "Информация об API")
    public ResponseEntity<Map<String, Object>> apiInfo() {
        return ResponseEntity.ok(Map.of(
                "name", "UTOWN Backend API",
                "version", "1.0.0",
                "description", "Korean Food Delivery Service"
        ));
    }
}
