package com.utown.service;

import com.utown.exception.BadRequestException;
import com.utown.exception.ForbiddenException;
import com.utown.exception.NotFoundException;
import com.utown.model.dto.order.CancelOrderRequest;
import com.utown.model.dto.order.CreateOrderRequest;
import com.utown.model.dto.order.OrderDTO;
import com.utown.model.dto.order.OrderItemDTO;
import com.utown.model.dto.order.UpdateOrderStatusRequest;
import com.utown.model.entity.Address;
import com.utown.model.entity.Cart;
import com.utown.model.entity.CartItem;
import com.utown.model.entity.Order;
import com.utown.model.entity.OrderItem;
import com.utown.model.entity.Restaurant;
import com.utown.model.entity.User;
import com.utown.model.enums.OrderStatus;
import com.utown.model.enums.PaymentStatus;
import com.utown.model.enums.UserRole;
import com.utown.repository.AddressRepository;
import com.utown.repository.CartRepository;
import com.utown.repository.OrderRepository;
import com.utown.repository.RestaurantRepository;
import com.utown.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;

    @Transactional
    public OrderDTO createOrder(Long userId, CreateOrderRequest request) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new BadRequestException("Cart is empty"));

        if (cart.getItems().isEmpty()) {
            throw new BadRequestException("Cart is empty");
        }

        Restaurant restaurant = cart.getRestaurant();
        if (!restaurant.getIsOpen()) {
            throw new BadRequestException("Restaurant is currently closed");
        }

        BigDecimal subtotal = cart.getItems().stream()
                .map(CartItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (subtotal.compareTo(restaurant.getMinOrderAmount()) < 0) {
            throw new BadRequestException(
                    "Order does not meet minimum amount: " + restaurant.getMinOrderAmount()
            );
        }

        Address address = addressRepository.findById(request.getAddressId())
                .orElseThrow(() -> new NotFoundException("Address not found"));

        if (!address.getUser().getId().equals(userId)) {
            throw new ForbiddenException("Address does not belong to user");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        BigDecimal deliveryFee = restaurant.getDeliveryFee();
        BigDecimal taxes = BigDecimal.ZERO;
        BigDecimal totalAmount = subtotal.add(deliveryFee).add(taxes);

        String orderNumber = generateOrderNumber();

        LocalDateTime estimatedDeliveryTime = LocalDateTime.now()
                .plusMinutes(restaurant.getEstimatedDeliveryTime() != null
                        ? restaurant.getEstimatedDeliveryTime()
                        : 45);

        Order order = Order.builder()
                .orderNumber(orderNumber)
                .user(user)
                .restaurant(restaurant)
                .address(address)
                .status(OrderStatus.PENDING)
                .subtotal(subtotal)
                .deliveryFee(deliveryFee)
                .discountAmount(BigDecimal.ZERO)
                .taxes(taxes)
                .totalAmount(totalAmount)
                .paymentMethod(request.getPaymentMethod())
                .paymentStatus(PaymentStatus.PENDING)
                .specialRequest(request.getSpecialRequest())
                .estimatedDeliveryTime(estimatedDeliveryTime)
                .build();

        for (CartItem cartItem : cart.getItems()) {
            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .menuItem(cartItem.getMenuItem())
                    .menuItemName(cartItem.getMenuItem().getName())
                    .quantity(cartItem.getQuantity())
                    .unitPrice(cartItem.getMenuItem().getPrice())
                    .selectedOptions(cartItem.getSelectedOptions())
                    .subtotal(cartItem.getSubtotal())
                    .build();
            order.addItem(orderItem);
        }

        order = orderRepository.save(order);

        cartRepository.delete(cart);

        log.info("Order {} created for user {}", orderNumber, userId);


        return mapToDTO(order);
    }

    @Transactional(readOnly = true)
    public Page<OrderDTO> getUserOrders(Long userId, Pageable pageable) {
        Page<Order> orders = orderRepository.findByUserId(userId, pageable);
        return orders.map(this::mapToDTO);
    }


    @Transactional(readOnly = true)
    public Page<OrderDTO> getRestaurantOrders(Long restaurantId, Long currentUserId, UserRole userRole, Pageable pageable) {
        if (userRole == UserRole.RESTAURANT_OWNER) {
            Restaurant restaurant = restaurantRepository.findById(restaurantId)
                    .orElseThrow(() -> new NotFoundException("Restaurant not found"));

            if (!restaurant.getOwner().getId().equals(currentUserId)) {
                throw new ForbiddenException("You don't have access to this restaurant's orders");
            }
        } else if (userRole != UserRole.ADMIN) {
            throw new ForbiddenException("Only restaurant owners and admins can view restaurant orders");
        }

        Page<Order> orders = orderRepository.findByRestaurantId(restaurantId, pageable);
        return orders.map(this::mapToDTO);
    }

    @Transactional(readOnly = true)
    public OrderDTO getOrderById(Long orderId, Long currentUserId, UserRole userRole) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found"));

        boolean hasAccess = false;

        if (userRole == UserRole.ADMIN) {
            hasAccess = true;
        } else if (userRole == UserRole.CLIENT && order.getUser().getId().equals(currentUserId)) {
            hasAccess = true;
        } else if (userRole == UserRole.RESTAURANT_OWNER &&
                order.getRestaurant().getOwner().getId().equals(currentUserId)) {
            hasAccess = true;
        }

        if (!hasAccess) {
            throw new ForbiddenException("You don't have access to this order");
        }

        return mapToDTO(order);
    }

    @Transactional
    public OrderDTO updateOrderStatus(Long orderId, Long currentUserId, UserRole userRole, UpdateOrderStatusRequest request) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found"));

        if (userRole == UserRole.RESTAURANT_OWNER) {
            if (!order.getRestaurant().getOwner().getId().equals(currentUserId)) {
                throw new ForbiddenException("You can only update your restaurant's orders");
            }
        } else if (userRole != UserRole.ADMIN) {
            throw new ForbiddenException("Only restaurant owners and admins can update order status");
        }

        if (!isValidStatusTransition(order.getStatus(), request.getStatus())) {
            throw new BadRequestException(
                    "Invalid status transition from " + order.getStatus() + " to " + request.getStatus()
            );
        }

        OrderStatus oldStatus = order.getStatus();
        order.setStatus(request.getStatus());

        if (request.getStatus() == OrderStatus.COMPLETED && order.getDeliveredAt() == null) {
            order.setDeliveredAt(LocalDateTime.now());
        }

        order = orderRepository.save(order);

        log.info("Order {} status updated from {} to {} by user {}",
                order.getOrderNumber(), oldStatus, request.getStatus(), currentUserId);


        return mapToDTO(order);
    }

    @Transactional
    public OrderDTO cancelOrder(Long orderId, Long currentUserId, CancelOrderRequest request) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found"));

        if (!order.getUser().getId().equals(currentUserId)) {
            throw new ForbiddenException("You can only cancel your own orders");
        }

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new BadRequestException("Only pending orders can be cancelled by customer");
        }

        order.setStatus(OrderStatus.CANCELLED);
        order.setCancellationReason(request.getReason());

        order = orderRepository.save(order);

        log.info("Order {} cancelled by user {}. Reason: {}",
                order.getOrderNumber(), currentUserId, request.getReason());

        return mapToDTO(order);
    }

    private boolean isValidStatusTransition(OrderStatus from, OrderStatus to) {
        return switch (from) {
            case PENDING -> to == OrderStatus.CONFIRMED || to == OrderStatus.CANCELLED;
            case CONFIRMED -> to == OrderStatus.PREPARING || to == OrderStatus.CANCELLED;
            case PREPARING -> to == OrderStatus.READY;
            case READY -> to == OrderStatus.DELIVERING;
            case DELIVERING -> to == OrderStatus.COMPLETED;
            case COMPLETED, CANCELLED -> false;
        };
    }

    private String generateOrderNumber() {
        String timestamp = LocalDateTime.now().format(
                java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd")
        );
        String uniqueId = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return "ORD-" + timestamp + "-" + uniqueId;
    }

    private OrderDTO mapToDTO(Order order) {
        return OrderDTO.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .status(order.getStatus())
                .user(OrderDTO.UserSummaryDTO.builder()
                        .id(order.getUser().getId())
                        .name(order.getUser().getName())
                        .phoneNumber(order.getUser().getPhoneNumber())
                        .build())
                .restaurant(OrderDTO.RestaurantSummaryDTO.builder()
                        .id(order.getRestaurant().getId())
                        .name(order.getRestaurant().getName())
                        .phone(order.getRestaurant().getPhone())
                        .build())
                .address(OrderDTO.AddressSummaryDTO.builder()
                        .id(order.getAddress().getId())
                        .address(order.getAddress().getAddress())
                        .detailAddress(order.getAddress().getDetailAddress())
                        .note(order.getAddress().getNote())
                        .build())
                .items(order.getItems().stream()
                        .map(this::mapOrderItemToDTO)
                        .collect(Collectors.toList()))
                .subtotal(order.getSubtotal())
                .deliveryFee(order.getDeliveryFee())
                .discountAmount(order.getDiscountAmount())
                .taxes(order.getTaxes())
                .totalAmount(order.getTotalAmount())
                .paymentMethod(order.getPaymentMethod())
                .paymentStatus(order.getPaymentStatus())
                .specialRequest(order.getSpecialRequest())
                .cancellationReason(order.getCancellationReason())
                .estimatedDeliveryTime(order.getEstimatedDeliveryTime())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .deliveredAt(order.getDeliveredAt())
                .build();
    }

    private OrderItemDTO mapOrderItemToDTO(OrderItem item) {
        return OrderItemDTO.builder()
                .id(item.getId())
                .menuItemName(item.getMenuItemName())
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .selectedOptions(item.getSelectedOptions())
                .subtotal(item.getSubtotal())
                .build();
    }
}
