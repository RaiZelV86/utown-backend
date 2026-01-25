package com.utown.controller;

import com.utown.model.dto.order.*;
import com.utown.model.enums.UserRole;
import com.utown.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(name = "Orders", description = "Order management")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @Operation(summary = "Create order", description = "Create a new order from cart")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<OrderDTO> createOrder(
            Authentication authentication,
            @Valid @RequestBody CreateOrderRequest request
    ) {
        Long userId = (Long) authentication.getPrincipal();
        OrderDTO order = orderService.createOrder(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }

    @GetMapping("/my")
    @Operation(summary = "Get my orders", description = "Get current user's orders")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<Page<OrderDTO>> getMyOrders(
            Authentication authentication,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Long userId = (Long) authentication.getPrincipal();
        Page<OrderDTO> orders = orderService.getUserOrders(userId, pageable);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/restaurant/{restaurantId}")
    @Operation(summary = "Get restaurant orders", description = "Get orders for a specific restaurant")
    @PreAuthorize("hasAnyRole('RESTAURANT_OWNER', 'ADMIN')")
    public ResponseEntity<Page<OrderDTO>> getRestaurantOrders(
            Authentication authentication,
            @PathVariable Long restaurantId,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Long userId = (Long) authentication.getPrincipal();
        UserRole userRole = extractUserRole(authentication);
        Page<OrderDTO> orders = orderService.getRestaurantOrders(restaurantId, userId, userRole, pageable);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{orderId}")
    @Operation(summary = "Get order details", description = "Get details of a specific order")
    public ResponseEntity<OrderDTO> getOrderById(
            Authentication authentication,
            @PathVariable Long orderId
    ) {
        Long userId = (Long) authentication.getPrincipal();
        UserRole userRole = extractUserRole(authentication);
        OrderDTO order = orderService.getOrderById(orderId, userId, userRole);
        return ResponseEntity.ok(order);
    }

    @PatchMapping("/{orderId}/status")
    @Operation(summary = "Update order status", description = "Update the status of an order (Restaurant owner or Admin)")
    @PreAuthorize("hasAnyRole('RESTAURANT_OWNER', 'ADMIN')")
    public ResponseEntity<OrderDTO> updateOrderStatus(
            Authentication authentication,
            @PathVariable Long orderId,
            @Valid @RequestBody UpdateOrderStatusRequest request
    ) {
        Long userId = (Long) authentication.getPrincipal();
        UserRole userRole = extractUserRole(authentication);
        OrderDTO order = orderService.updateOrderStatus(orderId, userId, userRole, request);
        return ResponseEntity.ok(order);
    }

    @PostMapping("/{orderId}/cancel")
    @Operation(summary = "Cancel order", description = "Cancel an order (Customer only, pending orders)")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<OrderDTO> cancelOrder(
            Authentication authentication,
            @PathVariable Long orderId,
            @Valid @RequestBody CancelOrderRequest request
    ) {
        Long userId = (Long) authentication.getPrincipal();
        OrderDTO order = orderService.cancelOrder(orderId, userId, request);
        return ResponseEntity.ok(order);
    }

    private UserRole extractUserRole(Authentication authentication) {
        String role = authentication.getAuthorities().stream()
                .findFirst()
                .map(grantedAuthority -> grantedAuthority.getAuthority().replace("ROLE_", ""))
                .orElse("CLIENT");
        return UserRole.valueOf(role);
    }
}
