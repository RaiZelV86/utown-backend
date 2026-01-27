package com.utown.model.dto.notification;

import com.utown.model.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderNotificationData {

    private Long orderId;
    private String orderNumber;
    private OrderStatus status;
    private OrderStatus previousStatus;
    private BigDecimal totalAmount;
    private String restaurantName;
    private Long restaurantId;
}
