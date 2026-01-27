package com.utown.controller;

import com.utown.model.dto.restaurant.UpdateRestaurantStatusRequest;
import com.utown.service.RestaurantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/restaurant-owner")
@RequiredArgsConstructor
@Tag(name="Restaurant Owner", description = "Endpoints for owner restaurants")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasAnyRole('RESTAURANT_OWNER','ADMIN')")
public class RestaurantOwnerController {

    private final RestaurantService restaurantService;

    @PatchMapping("/restaurants/{id}/status")
    @Operation(
            summary = "Изменить статус ресторана (открыт/закрыт)",
            description = "Владелец ресторана или ADMIN могут изменить статус"
    )
    public ResponseEntity<Map<String, Object>> updateRestaurantStatus(
            @Parameter(description = "ID ресторана")
            @PathVariable Long id,

            @Valid @RequestBody UpdateRestaurantStatusRequest request,

            Authentication authentication
    ) {
        Long currentUserId = (Long) authentication.getPrincipal();

        restaurantService.updateRestaurantStatus(id, currentUserId, request.getIsOpen());

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Restaurant status updated successfully",
                "isOpen", request.getIsOpen()
        ));
    }

    @GetMapping("/restaurants/my")
    @Operation(summary = "My restaurants", description = "Get to list owner's restaurants ")
    public ResponseEntity<Map<String, Object>> getMyRestaurants(Authentication authentication) {
        Long currentUserId = (Long) authentication.getPrincipal();

        var restaurants = restaurantService.getRestaurantsByOwnerId(currentUserId);
        
        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", restaurants
        ));
    }
}
