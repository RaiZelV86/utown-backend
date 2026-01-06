package com.utown.model.dto.restaurant;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateRestaurantStatusRequest {

    @NotNull(message = "isOpen field is required")
    private Boolean isOpen;

    private String reason;
}
