package com.utown.model.dto.restaurant;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateRestaurantRequest {
    @NotNull(message = "Category ID is required")
    private Long categoryId;

    @NotBlank(message = "Restaurant name is required")
    @Size(max = 200, message = "Name must not exceed 200 characters")
    private String name;

    private String description;

    @NotBlank(message = "Address is required")
    private String address;

    @NotBlank(message = "City is required")
    private String city;

    @NotNull(message = "Latitude is required")
    @DecimalMin(value = "-90.0", message = "Latitude must be between -90 and 90")
    @DecimalMax(value = "90.0", message = "Latitude must be between -90 and 90")
    private BigDecimal latitude;

    @NotNull(message = "Longitude is required")
    @DecimalMin(value = "-180.0", message = "Longitude must be between -180 and 180")
    @DecimalMax(value = "180.0", message = "Longitude must be between -180 and 180")
    private BigDecimal longitude;

    @NotBlank(message = "Phone is required")
    private String phone;

    private String imageUrl;
    private String bannerImageUrl;

    @DecimalMin(value = "0", message = "Minimum order amount must be >= 0")
    private BigDecimal minOrderAmount;

    @DecimalMin(value = "0", message = "Delivery fee must be >= 0")
    private BigDecimal deliveryFee;

    @Min(value = 0, message = "Estimated delivery time must be >= 0")
    private Integer estimatedDeliveryTime;

    private String openingHours;

    private Long ownerId;
}
