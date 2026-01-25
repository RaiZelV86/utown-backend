package com.utown.controller;

import com.utown.model.dto.cart.AddToCartRequest;
import com.utown.model.dto.cart.CartDTO;
import com.utown.model.dto.cart.UpdateCartItemRequest;
import com.utown.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@Tag(name = "Cart", description = "Shopping cart management")
public class CartController {

    private final CartService cartService;

    @GetMapping
    @Operation(summary = "Get user's cart", description = "Retrieve current user's shopping cart")
    public ResponseEntity<CartDTO> getCart(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        CartDTO cart = cartService.getCart(userId);
        return ResponseEntity.ok(cart);
    }

    @PostMapping("/items")
    @Operation(summary = "Add item to cart", description = "Add a menu item to the shopping cart")
    public ResponseEntity<CartDTO> addItemToCart(
            Authentication authentication,
            @Valid @RequestBody AddToCartRequest request
    ) {
        Long userId = (Long) authentication.getPrincipal();
        CartDTO cart = cartService.addItemToCart(userId, request);
        return ResponseEntity.ok(cart);
    }

    @PutMapping("/items/{cartItemId}")
    @Operation(summary = "Update cart item quantity", description = "Update the quantity of a cart item")
    public ResponseEntity<CartDTO> updateCartItemQuantity(
            Authentication authentication,
            @PathVariable Long cartItemId,
            @Valid @RequestBody UpdateCartItemRequest request
    ) {
        Long userId = (Long) authentication.getPrincipal();
        CartDTO cart = cartService.updateCartItemQuantity(userId, cartItemId, request);
        return ResponseEntity.ok(cart);
    }

    @DeleteMapping("/items/{cartItemId}")
    @Operation(summary = "Remove item from cart", description = "Remove a specific item from the cart")
    public ResponseEntity<CartDTO> removeCartItem(
            Authentication authentication,
            @PathVariable Long cartItemId
    ) {
        Long userId = (Long) authentication.getPrincipal();
        CartDTO cart = cartService.removeCartItem(userId, cartItemId);
        return ResponseEntity.ok(cart);
    }

    @DeleteMapping
    @Operation(summary = "Clear cart", description = "Remove all items from the cart")
    public ResponseEntity<Void> clearCart(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        cartService.clearCart(userId);
        return ResponseEntity.noContent().build();
    }
}