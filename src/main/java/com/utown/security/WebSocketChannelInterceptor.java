package com.utown.security;

import com.utown.model.entity.User;
import com.utown.model.enums.UserRole;
import com.utown.repository.OrderRepository;
import com.utown.repository.RestaurantEmployeeRepository;
import com.utown.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RequiredArgsConstructor
@Slf4j
@Component
public class WebSocketChannelInterceptor implements ChannelInterceptor {

    private final RestaurantRepository restaurantRepository;
    private final RestaurantEmployeeRepository restaurantEmployeeRepository;
    private final OrderRepository orderRepository;

    private static final Pattern RESTAURANT_PATTERN = Pattern.compile("/topic/restaurants/(\\d+)/.*");
    private static final Pattern ORDER_PATTERN = Pattern.compile("/topic/orders/(\\d+)$");

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor != null && StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
            String destination = accessor.getDestination();
            Authentication authentication = (Authentication) accessor.getUser();

            if (authentication != null && destination != null) {
                validateSubscription(destination, authentication);
            }
        }

        return message;
    }

    private void validateSubscription(String destination, Authentication authentication) {
        User user = (User) authentication.getPrincipal();

        log.debug("User {} ({}) subscribing to {}",
                user.getPhoneNumber(), user.getRole(), destination);

        Matcher restaurantMatcher = RESTAURANT_PATTERN.matcher(destination);
        if (restaurantMatcher.matches()) {
            Long restaurantId = Long.parseLong(restaurantMatcher.group(1));
            validateRestaurantAccess(user, restaurantId);
            return;
        }

        Matcher orderMatcher = ORDER_PATTERN.matcher(destination);
        if (orderMatcher.matches()) {
            Long orderId = Long.parseLong(orderMatcher.group(1));
            validateOrderAccess(user, orderId);
            return;
        }

        if (destination.startsWith("/user/queue/")) {
            return;
        }

        log.warn("Unknown destination pattern: {}", destination);
    }

    private void validateRestaurantAccess(User user, Long restaurantId) {
        if (user.getRole() == UserRole.ADMIN) {
            return;
        }

        if (user.getRole() == UserRole.RESTAURANT_OWNER) {
            boolean isOwner = restaurantRepository.existsByIdAndOwnerId(restaurantId, user.getId());
            if (!isOwner) {
                throw new SecurityException(
                        "Access denied: You are not the owner of restaurant " + restaurantId
                );
            }
            return;
        }

        boolean isEmployee = restaurantEmployeeRepository.existsByRestaurantIdAndUserId(
                restaurantId, user.getId()
        );
        if (isEmployee) {
            return;
        }

        throw new SecurityException(
                "Access denied: Only restaurant owners, staff, and admins can subscribe to restaurant topics"
        );
    }

    private void validateOrderAccess(User user, Long orderId) {
        if (user.getRole() == UserRole.ADMIN) {
            return;
        }

        if (user.getRole() == UserRole.CLIENT) {
            boolean isOwnOrder = orderRepository.existsByIdAndUserId(orderId, user.getId());
            if (!isOwnOrder) {
                throw new SecurityException(
                        "Access denied: This is not your order"
                );
            }
            return;
        }

        if (user.getRole() == UserRole.RESTAURANT_OWNER) {
            boolean isRestaurantOrder = orderRepository.existsByIdAndRestaurantOwnerId(
                    orderId, user.getId()
            );
            if (!isRestaurantOrder) {
                throw new SecurityException(
                        "Access denied: This order is not for your restaurant"
                );
            }
            return;
        }

        if (user.getRole() == UserRole.RIDER) {
            return;
        }

        boolean isRestaurantEmployee = orderRepository.findRestaurantIdByOrderId(orderId)
                .map(restaurantId -> restaurantEmployeeRepository.existsByRestaurantIdAndUserId(
                        restaurantId, user.getId()))
                .orElse(false);
        if (isRestaurantEmployee) {
            return;
        }

        throw new SecurityException(
                "Access denied: You don't have permission to subscribe to this order"
        );
    }
}
