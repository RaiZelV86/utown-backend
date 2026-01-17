package com.utown.controller;

import com.utown.model.dto.restaurant.MenuItemDTO;
import com.utown.model.dto.restaurant.RestaurantMenuDTO;
import com.utown.service.MenuItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
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
}
