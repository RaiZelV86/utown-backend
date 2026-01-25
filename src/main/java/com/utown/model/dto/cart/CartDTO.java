package com.utown.model.dto.cart;

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
public class CartDTO {

    private Long id;
    private Long userId;
    private RestaurantSummaryDTO restaurant;
    private List<CartItemDTO> items;
    private CartSummaryDTO summary;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RestaurantSummaryDTO {
        private Long id;
        private String name;
        private BigDecimal minOrderAmount;
        private BigDecimal deliveryFee;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CartSummaryDTO {
        private BigDecimal subtotal;
        private BigDecimal deliveryFee;
        private BigDecimal total;
        private Integer itemCount;
        private Boolean meetsMinimum;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CartItemDTO {
        private Long id;
        private MenuItemSummaryDTO menuItem;
        private Integer quantity;
        private String selectedOptions;
        private BigDecimal subtotal;

        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class MenuItemSummaryDTO {
            private Long id;
            private String name;
            private BigDecimal price;
            private String imageUrl;
            private Boolean isAvailable;
        }
    }
}