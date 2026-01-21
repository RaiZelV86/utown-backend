package com.utown.model.dto.restaurant;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CreateMenuItemRequest {

    @NotNull(message = "Restaurant ID is required")
    private Long restaurantId;

    @NotBlank(message = "Name is required")
    private String name;

    private String description;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", message = "Price must be greater than or equal to 0")
    private BigDecimal price;

    private String categoryName;

    private String imageUrl;

    private Boolean isAvailable = true;

    private Boolean isSpicy = false;

    private Integer spicyLevel;

    private Integer sortOrder = 0;

    private List<CreateMenuItemOptionRequest> options;
}
