package com.utown.controller;

import com.utown.model.dto.restaurant.CreateMenuItemRequest;
import com.utown.model.dto.restaurant.MenuItemDTO;
import com.utown.model.dto.restaurant.RestaurantMenuDTO;
import com.utown.model.dto.restaurant.UpdateMenuItemRequest;
import com.utown.service.MenuItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Menu Items", description = "Menu item management endpoints")
public class MenuItemController {

    private final MenuItemService menuItemService;

    @GetMapping("/restaurants/{id}/menu")
    @Operation(
            summary = "Get restaurant menu",
            description = "Get restaurant menu grouped by categories with optional filters"
    )
    public ResponseEntity<RestaurantMenuDTO> getRestaurantMenu(
            @Parameter(description = "Restaurant ID")
            @PathVariable Long id,

            @Parameter(description = "Filter by category (optional)")
            @RequestParam(required = false) String category,

            @Parameter(description = "Filter by availability (optional)")
            @RequestParam(required = false) Boolean available
    ) {
        RestaurantMenuDTO menu = menuItemService.getRestaurantMenu(id, category, available);
        return ResponseEntity.ok(menu);
    }

    @GetMapping("/restaurants/{id}/menu-items")
    @Operation(
            summary = "Get restaurant menu items",
            description = "Get all menu items of a restaurant without grouping"
    )
    public ResponseEntity<List<MenuItemDTO>> getRestaurantMenuItems(
            @Parameter(description = "Restaurant ID")
            @PathVariable Long id
    ) {
        List<MenuItemDTO> menuItems = menuItemService.getRestaurantMenuItems(id);
        return ResponseEntity.ok(menuItems);
    }

    @GetMapping("/menu-items/{id}")
    @Operation(
            summary = "Get menu item by ID",
            description = "Get detailed information about a specific menu item"
    )
    public ResponseEntity<MenuItemDTO> getMenuItem(
            @Parameter(description = "Menu item ID")
            @PathVariable Long id
    ) {
        MenuItemDTO menuItem = menuItemService.getMenuItem(id);
        return ResponseEntity.ok(menuItem);
    }

    @PostMapping("/menu-items")
    @PreAuthorize("hasAnyRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "Create menu item",
            description = "Create a new menu item (Restaurant Owner or Admin only)"
    )
    public ResponseEntity<MenuItemDTO> createMenuItem(
            @Valid @RequestBody CreateMenuItemRequest request,
            Authentication authentication
    ) {
        Long currentUserId = (Long) authentication.getPrincipal();
        MenuItemDTO created = menuItemService.createMenuItem(request, currentUserId);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/menu-items/{id}")
    @PreAuthorize("hasAnyRole('RESTAURANT_OWNER', 'ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "Update menu item",
            description = "Update an existing menu item (Restaurant Owner or Admin only)"
    )
    public ResponseEntity<MenuItemDTO> updateMenuItem(
            @Parameter(description = "Menu item ID")
            @PathVariable Long id,
            @Valid @RequestBody UpdateMenuItemRequest request,
            Authentication authentication
    ) {
        Long currentUserId = (Long) authentication.getPrincipal();
        MenuItemDTO updated = menuItemService.updateMenuItem(id, request, currentUserId);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/menu-items/{id}")
    @PreAuthorize("hasAnyRole('RESTAURANT_OWNER', 'ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "Delete menu item",
            description = "Delete a menu item (Restaurant Owner or Admin only)"
    )
    public ResponseEntity<Void> deleteMenuItem(
            @Parameter(description = "Menu item ID")
            @PathVariable Long id,
            Authentication authentication
    ) {
        Long currentUserId = (Long) authentication.getPrincipal();
        menuItemService.deleteMenuItem(id, currentUserId);
        return ResponseEntity.noContent().build();
    }
}
