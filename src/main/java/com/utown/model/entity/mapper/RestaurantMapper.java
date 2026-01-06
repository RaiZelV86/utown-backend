package com.utown.model.entity.mapper;

import com.utown.model.dto.restaurant.RestaurantDto;
import com.utown.model.entity.Restaurant;
import org.springframework.stereotype.Component;

@Component
public class RestaurantMapper {

    public RestaurantDto toDto(Restaurant restaurant) {
        if (restaurant == null) {
            return null;
        }

        return RestaurantDto.builder()
                .id(restaurant.getId())
                .ownerId(restaurant.getOwner().getId())
                .ownerName(restaurant.getOwner().getName())
                .categoryId(restaurant.getCategory().getId())
                .categoryName(restaurant.getCategory().getName())
                .name(restaurant.getName())
                .description(restaurant.getDescription())
                .address(restaurant.getAddress())
                .city(restaurant.getCity())
                .latitude(restaurant.getLatitude())
                .longitude(restaurant.getLongitude())
                .phone(restaurant.getPhone())
                .imageUrl(restaurant.getImageUrl())
                .bannerImageUrl(restaurant.getBannerImageUrl())
                .rating(restaurant.getRating())
                .reviewCount(restaurant.getReviewCount())
                .minOrderAmount(restaurant.getMinOrderAmount())
                .deliveryFee(restaurant.getDeliveryFee())
                .estimatedDeliveryTime(restaurant.getEstimatedDeliveryTime())
                .openingHours(restaurant.getOpeningHours())
                .isOpen(restaurant.getIsOpen())
                .isFeatured(restaurant.getIsFeatured())
                .isActive(restaurant.getIsActive())
                .createdAt(restaurant.getCreatedAt())
                .build();
    }
}
