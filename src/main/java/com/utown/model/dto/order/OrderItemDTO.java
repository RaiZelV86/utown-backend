package com.utown.model.dto.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemDTO {

    private Long id;
    private String menuItemName;
    private Integer quantity;
    private BigDecimal unitPrice;
    private String selectedOptions;
    private BigDecimal subtotal;
}