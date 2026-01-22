package com.utown.model.dto.order;

import com.utown.model.enums.OrderStatus;
import com.utown.model.enums.PaymentMethod;
import com.utown.model.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {

    private Long id;
    private String orderNumber;
    private OrderStatus status;
    private UserSummaryDTO user;
    private RestaurantSummaryDTO restaurant;
    private AddressSummaryDTO address;
    private List<com.utown.model.dto.order.OrderItemDTO> items;
    private BigDecimal subtotal;
    private BigDecimal deliveryFee;
    private BigDecimal discountAmount;
    private BigDecimal taxes;
    private BigDecimal totalAmount;
    private PaymentMethod paymentMethod;
    private PaymentStatus paymentStatus;
    private String specialRequest;
    private String cancellationReason;
    private LocalDateTime estimatedDeliveryTime;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deliveredAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserSummaryDTO {
        private Long id;
        private String name;
        private String phoneNumber;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RestaurantSummaryDTO {
        private Long id;
        private String name;
        private String phone;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AddressSummaryDTO {
        private Long id;
        private String address;
        private String detailAddress;
        private String note;
    }
}