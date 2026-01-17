package com.utown.model.dto.restaurant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MenuItemDTO {

    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private String imageUrl;
    private Boolean isAvailable;
    private Boolean isSpicy;
    private Integer spicyLevel;
    private String categoryName;
    private List<MenuItemOptionDTO> options;
}
