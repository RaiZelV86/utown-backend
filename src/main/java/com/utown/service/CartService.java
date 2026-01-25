package com.utown.service;

import com.utown.exception.BadRequestException;
import com.utown.exception.ConflictException;
import com.utown.exception.NotFoundException;
import com.utown.model.dto.cart.AddToCartRequest;
import com.utown.model.dto.cart.CartDTO;
import com.utown.model.dto.cart.UpdateCartItemRequest;
import com.utown.model.entity.Cart;
import com.utown.model.entity.CartItem;
import com.utown.model.entity.MenuItem;
import com.utown.model.entity.Restaurant;
import com.utown.model.entity.User;
import com.utown.repository.CartItemRepository;
import com.utown.repository.CartRepository;
import com.utown.repository.MenuItemRepository;
import com.utown.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final MenuItemRepository menuItemRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public CartDTO getCart(Long userId) {
        Cart cart = cartRepository.findByUserId(userId).orElse(null);

        if (cart == null) {
            return createEmptyCartDTO();
        }

        return mapToDTO(cart);
    }

    @Transactional
    public CartDTO addItemToCart(Long userId, AddToCartRequest request) {
        MenuItem menuItem = menuItemRepository.findById(request.getMenuItemId())
                .orElseThrow(() -> new NotFoundException("Menu item not found"));

        if (!menuItem.getIsAvailable()) {
            throw new BadRequestException("Menu item is not available");
        }

        Cart cart = cartRepository.findByUserId(userId).orElse(null);

        if (cart == null) {

            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new NotFoundException("User not found"));

            cart = Cart.builder()
                    .user(user)
                    .restaurant(menuItem.getRestaurant())
                    .build();
            cart = cartRepository.save(cart);

        } else {
            if (!cart.getRestaurant().getId().equals(menuItem.getRestaurant().getId())) {
                throw new ConflictException(
                        "Cart already contains items from another restaurant. Clear cart first.",
                        "DIFFERENT_RESTAURANT"
                );
            }
        }

        CartItem existingItem = cartItemRepository
                .findByCartIdAndMenuItemId(cart.getId(), menuItem.getId())
                .orElse(null);

        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + request.getQuantity());
            cartItemRepository.save(existingItem);
        } else {
            CartItem newItem = CartItem.builder()
                    .cart(cart)
                    .menuItem(menuItem)
                    .quantity(request.getQuantity())
                    .selectedOptions(request.getSelectedOptions())
                    .build();
            cart.addItem(newItem);
            cartItemRepository.save(newItem);
        }

        log.info("Added item {} to cart for user {}", menuItem.getId(), userId);

        return mapToDTO(cart);
    }

    @Transactional
    public CartDTO updateCartItemQuantity(Long userId, Long cartItemId, UpdateCartItemRequest request) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new NotFoundException("Cart item not found"));

        if (!cartItem.getCart().getUser().getId().equals(userId)) {
            throw new BadRequestException("Cart item does not belong to user");
        }

        if (request.getQuantity() <= 0) {
            throw new BadRequestException("Quantity must be greater than 0");
        }

        cartItem.setQuantity(request.getQuantity());
        cartItemRepository.save(cartItem);

        log.info("Updated cart item {} quantity to {} for user {}", cartItemId, request.getQuantity(), userId);

        return mapToDTO(cartItem.getCart());
    }

    @Transactional
    public CartDTO removeCartItem(Long userId, Long cartItemId) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new NotFoundException("Cart item not found"));

        if (!cartItem.getCart().getUser().getId().equals(userId)) {
            throw new BadRequestException("Cart item does not belong to user");
        }

        Cart cart = cartItem.getCart();
        cart.removeItem(cartItem);
        cartItemRepository.delete(cartItem);

        if (cart.getItems().isEmpty()) {
            cartRepository.delete(cart);
            log.info("Deleted empty cart for user {}", userId);
            return createEmptyCartDTO();
        }

        log.info("Removed cart item {} for user {}", cartItemId, userId);

        return mapToDTO(cart);
    }

    @Transactional
    public void clearCart(Long userId) {
        Cart cart = cartRepository.findByUserId(userId).orElse(null);

        if (cart != null) {
            cartRepository.delete(cart);
            log.info("Cleared cart for user {}", userId);
        }
    }

    private CartDTO mapToDTO(Cart cart) {
        if (cart == null) {
            return createEmptyCartDTO();
        }

        Restaurant restaurant = cart.getRestaurant();

        BigDecimal subtotal = cart.getItems().stream()
                .map(CartItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal deliveryFee = restaurant.getDeliveryFee();
        BigDecimal total = subtotal.add(deliveryFee);

        int itemCount = cart.getItems().stream()
                .mapToInt(CartItem::getQuantity)
                .sum();

        boolean meetsMinimum = subtotal.compareTo(restaurant.getMinOrderAmount()) >= 0;

        return CartDTO.builder()
                .id(cart.getId())
                .userId(cart.getUser().getId())
                .restaurant(CartDTO.RestaurantSummaryDTO.builder()
                        .id(restaurant.getId())
                        .name(restaurant.getName())
                        .minOrderAmount(restaurant.getMinOrderAmount())
                        .deliveryFee(restaurant.getDeliveryFee())
                        .build())
                .items(cart.getItems().stream()
                        .map(this::mapCartItemToDTO)
                        .collect(Collectors.toList()))
                .summary(CartDTO.CartSummaryDTO.builder()
                        .subtotal(subtotal)
                        .deliveryFee(deliveryFee)
                        .total(total)
                        .itemCount(itemCount)
                        .meetsMinimum(meetsMinimum)
                        .build())
                .createdAt(cart.getCreatedAt())
                .updatedAt(cart.getUpdatedAt())
                .build();
    }

    private CartDTO.CartItemDTO mapCartItemToDTO(CartItem item) {
        MenuItem menuItem = item.getMenuItem();

        return CartDTO.CartItemDTO.builder()
                .id(item.getId())
                .menuItem(CartDTO.CartItemDTO.MenuItemSummaryDTO.builder()
                        .id(menuItem.getId())
                        .name(menuItem.getName())
                        .price(menuItem.getPrice())
                        .imageUrl(menuItem.getImageUrl())
                        .isAvailable(menuItem.getIsAvailable())
                        .build())
                .quantity(item.getQuantity())
                .selectedOptions(item.getSelectedOptions())
                .subtotal(item.getSubtotal())
                .build();
    }

    private CartDTO createEmptyCartDTO() {
        return CartDTO.builder()
                .items(java.util.Collections.emptyList())
                .summary(CartDTO.CartSummaryDTO.builder()
                        .subtotal(BigDecimal.ZERO)
                        .deliveryFee(BigDecimal.ZERO)
                        .total(BigDecimal.ZERO)
                        .itemCount(0)
                        .meetsMinimum(false)
                        .build())
                .build();
    }
}
