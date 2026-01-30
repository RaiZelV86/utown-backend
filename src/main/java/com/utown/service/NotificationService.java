package com.utown.service;

import com.utown.constant.NotificationTopics;
import com.utown.model.dto.notification.NotificationDTO;
import com.utown.model.dto.notification.OrderNotificationData;
import com.utown.model.entity.Order;
import com.utown.model.enums.NotificationType;
import com.utown.model.enums.OrderStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    public void sendOrderCreatedNotification(Order order) {
        log.info("Sending order created notification for order: {}", order.getOrderNumber());

        OrderNotificationData data = OrderNotificationData.builder()
                .orderId(order.getId())
                .orderNumber(order.getOrderNumber())
                .status(order.getStatus())
                .totalAmount(order.getTotalAmount())
                .restaurantName(order.getRestaurant().getName())
                .restaurantId(order.getRestaurant().getId())
                .build();

        // Уведомление для ресторана (все сотрудники получат через топик)
        NotificationDTO restaurantNotification = NotificationDTO.builder()
                .type(NotificationType.ORDER_CREATED)
                .title("New Order Received")
                .message(String.format("New order #%s has been placed", order.getOrderNumber()))
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();

        // Отправка в топик ресторана (для всех сотрудников)
        sendToRestaurantTopic(order.getRestaurant().getId(), restaurantNotification);

        // Отправка в топик конкретного заказа (для клиента и ресторана)
        sendToOrderTopic(order.getId(), restaurantNotification);

        // Персональное уведомление клиенту
        sendToUser(order.getUser().getId(), NotificationDTO.builder()
                .type(NotificationType.ORDER_CREATED)
                .title("Order Placed Successfully")
                .message(String.format("Your order #%s has been placed successfully", order.getOrderNumber()))
                .data(data)
                .timestamp(LocalDateTime.now())
                .build());

        log.info("Order created notifications sent successfully");
    }

    public void sendOrderStatusChangedNotification(Order order, OrderStatus previousStatus) {
        log.info("Sending order status changed notification: {} -> {}", previousStatus, order.getStatus());

        OrderNotificationData data = OrderNotificationData.builder()
                .orderId(order.getId())
                .orderNumber(order.getOrderNumber())
                .status(order.getStatus())
                .previousStatus(previousStatus)
                .totalAmount(order.getTotalAmount())
                .restaurantName(order.getRestaurant().getName())
                .restaurantId(order.getRestaurant().getId())
                .build();

        NotificationType notificationType = mapOrderStatusToNotificationType(order.getStatus());
        String message = getStatusChangeMessage(order.getOrderNumber(), order.getStatus());

        NotificationDTO notification = NotificationDTO.builder()
                .type(notificationType)
                .title("Order Status Updated")
                .message(message)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();

        // Отправка в топик ресторана (для всех сотрудников)
        sendToRestaurantTopic(order.getRestaurant().getId(), notification);

        // Отправка в топик конкретного заказа (для клиента и ресторана)
        sendToOrderTopic(order.getId(), notification);

        // Персональное уведомление клиенту
        sendToUser(order.getUser().getId(), notification);

        log.info("Order status changed notifications sent successfully");
    }

    public void sendToRestaurantTopic(Long restaurantId, NotificationDTO notification) {
        try {
            String destination = NotificationTopics.restaurantOrders(restaurantId);
            messagingTemplate.convertAndSend(destination, notification);
            log.debug("Notification sent to restaurant {} topic: {}", restaurantId, notification.getType());
        } catch (Exception e) {
            log.error("Failed to send notification to restaurant {} topic: {}", restaurantId, e.getMessage());
        }
    }

    public void sendToOrderTopic(Long orderId, NotificationDTO notification) {
        try {
            String destination = NotificationTopics.orderUpdates(orderId);
            messagingTemplate.convertAndSend(destination, notification);
            log.debug("Notification sent to order {} topic: {}", orderId, notification.getType());
        } catch (Exception e) {
            log.error("Failed to send notification to order {} topic: {}", orderId, e.getMessage());
        }
    }

    private void sendToUser(Long userId, NotificationDTO notification) {
        try {
            messagingTemplate.convertAndSendToUser(
                    userId.toString(),
                    NotificationTopics.USER_NOTIFICATIONS_QUEUE,
                    notification
            );
            log.debug("Notification sent to user {}: {}", userId, notification.getType());
        } catch (Exception e) {
            log.error("Failed to send notification to user {}: {}", userId, e.getMessage());
        }
    }

    public void broadcastNotification(NotificationDTO notification) {
        try {
            messagingTemplate.convertAndSend("/topic/notifications", notification);
            log.debug("Notification broadcasted: {}", notification.getType());
        } catch (Exception e) {
            log.error("Failed to broadcast notification: {}", e.getMessage());
        }
    }

    private NotificationType mapOrderStatusToNotificationType(OrderStatus status) {
        return switch (status) {
            case PENDING -> NotificationType.ORDER_CREATED;
            case CONFIRMED -> NotificationType.ORDER_CONFIRMED;
            case PREPARING -> NotificationType.ORDER_PREPARING;
            case READY -> NotificationType.ORDER_READY;
            case DELIVERING -> NotificationType.ORDER_DELIVERING;
            case COMPLETED -> NotificationType.ORDER_COMPLETED;
            case CANCELLED -> NotificationType.ORDER_CANCELLED;
        };
    }

    private String getStatusChangeMessage(String orderNumber, OrderStatus status) {
        return switch (status) {
            case CONFIRMED -> String.format("Order #%s has been confirmed by the restaurant", orderNumber);
            case PREPARING -> String.format("Order #%s is being prepared", orderNumber);
            case READY -> String.format("Order #%s is ready for pickup/delivery", orderNumber);
            case DELIVERING -> String.format("Order #%s is out for delivery", orderNumber);
            case COMPLETED -> String.format("Order #%s has been delivered successfully", orderNumber);
            case CANCELLED -> String.format("Order #%s has been cancelled", orderNumber);
            default -> String.format("Order #%s status updated to %s", orderNumber, status);
        };
    }
}
