package com.utown.model.dto.restaurant;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RestaurantDto {

    private Long id;
    private Long ownerId;
    private String ownerName;
    private Long categoryId;
    private String categoryName;
    private String name;
    private String description;
    private String address;
    private String city;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String phone;
    private String imageUrl;
    private String bannerImageUrl;
    private BigDecimal rating;
    private Integer reviewCount;
    private BigDecimal minOrderAmount;
    private BigDecimal deliveryFee;
    private Integer estimatedDeliveryTime;
    private String openingHours;
    private Boolean isOpen;
    private Boolean isFeatured;
    private Boolean isActive;
    private LocalDateTime createdAt;
}
