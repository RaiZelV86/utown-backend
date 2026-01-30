package com.utown.model.dto.notification;

import com.utown.model.enums.NotificationType;
import com.utown.model.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDTO {

    private NotificationType type;
    private String title;
    private String message;
    private Object data;
    private LocalDateTime timestamp;
    private Long orderId;
    private String orderNumber;
    private OrderStatus oldStatus;
    private OrderStatus newStatus;
    private LocalDateTime estimatedDeliveryTime;
    private Long restaurantId;

}
