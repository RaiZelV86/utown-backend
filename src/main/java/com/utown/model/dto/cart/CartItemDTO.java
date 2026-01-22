package com.utown.model.dto.cart;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItemDTO {

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