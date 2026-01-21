package com.utown.model.dto.restaurant;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateMenuItemOptionRequest {

    @NotBlank(message = "Option type is required")
    private String optionType;

    @NotBlank(message = "Name is required")
    private String name;

    @NotNull(message = "Additional price is required")
    @DecimalMin(value = "0.0", message = "Additional price must be greater than or equal to 0")
    private BigDecimal additionalPrice;

    private Boolean isAvailable = true;
}
