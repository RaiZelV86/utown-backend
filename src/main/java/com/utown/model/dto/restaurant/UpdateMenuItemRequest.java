package com.utown.model.dto.restaurant;

import jakarta.validation.constraints.DecimalMin;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class UpdateMenuItemRequest {

    private String name;

    private String description;

    @DecimalMin(value = "0.0", message = "Price must be greater than or equal to 0")
    private BigDecimal price;

    private String categoryName;

    private String imageUrl;

    private Boolean isAvailable;

    private Boolean isSpicy;

    private Integer spicyLevel;

    private Integer sortOrder;

    private List<CreateMenuItemOptionRequest> options;
}
