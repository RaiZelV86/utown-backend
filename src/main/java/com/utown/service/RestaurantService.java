package com.utown.service;

import com.utown.exception.NotFoundException;
import com.utown.model.dto.restaurant.CreateRestaurantRequest;
import com.utown.model.dto.restaurant.RestaurantDto;
import com.utown.model.dto.restaurant.UpdateRestaurantRequest;
import com.utown.model.entity.Category;
import com.utown.model.entity.Restaurant;
import com.utown.model.entity.User;
import com.utown.repository.CategoryRepository;
import com.utown.repository.RestaurantRepository;
import com.utown.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    @Transactional
    public RestaurantDto createRestaurant(CreateRestaurantRequest request, Long ownerId) {
        log.info("Creating restaurant: name={}, ownerId={}", request.getName(), ownerId);

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new NotFoundException("Category not found"));

        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("Owner not found"));

        Restaurant restaurant = Restaurant.builder()
                .owner(owner)
                .category(category)
                .name(request.getName())
                .description(request.getDescription())
                .address(request.getAddress())
                .city(request.getCity())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .phone(request.getPhone())
                .imageUrl(request.getImageUrl())
                .bannerImageUrl(request.getBannerImageUrl())
                .rating(BigDecimal.ZERO)
                .reviewCount(0)
                .minOrderAmount(request.getMinOrderAmount() != null ? request.getMinOrderAmount() : BigDecimal.ZERO)
                .deliveryFee(request.getDeliveryFee() != null ? request.getDeliveryFee() : BigDecimal.ZERO)
                .estimatedDeliveryTime(request.getEstimatedDeliveryTime())
                .isOpen(false)
                .isFeatured(false)
                .isActive(true)
                .build();

        restaurant = restaurantRepository.save(restaurant);

        log.info("Restaurant created successfully: id={}", restaurant.getId());

        return mapToResponse(restaurant);
    }

    @Transactional(readOnly = true)
    public Page<RestaurantDto> getAllRestaurants(Pageable pageable) {
        log.info("Getting all restaurants, page={}", pageable.getPageNumber());

        Page<Restaurant> restaurants = restaurantRepository.findAllWithCategory(pageable);

        return restaurants.map(this::mapToResponse);
    }

    @Transactional(readOnly = true)
    public RestaurantDto getRestaurantById(Long id) {
        log.info("Getting restaurant by id={}", id);

        Restaurant restaurant = restaurantRepository.findByIdWithCategory(id)
                .orElseThrow(() -> new NotFoundException("Restaurant not found"));

        return mapToResponse(restaurant);
    }

    @Transactional
        public RestaurantDto updateRestaurant(Long id, UpdateRestaurantRequest request) {
        log.info("Updating restaurant: id={}", id);

        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Restaurant not found"));

        if (request.getName() != null) {
            restaurant.setName(request.getName());
        }
        if (request.getDescription() != null) {
            restaurant.setDescription(request.getDescription());
        }
        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new NotFoundException("Category not found"));
            restaurant.setCategory(category);
        }
        if (request.getAddress() != null) {
            restaurant.setAddress(request.getAddress());
        }
        if (request.getCity() != null) {
            restaurant.setCity(request.getCity());
        }
        if (request.getLatitude() != null) {
            restaurant.setLatitude(request.getLatitude());
        }
        if (request.getLongitude() != null) {
            restaurant.setLongitude(request.getLongitude());
        }
        if (request.getPhone() != null) {
            restaurant.setPhone(request.getPhone());
        }
        if (request.getMinOrderAmount() != null) {
            restaurant.setMinOrderAmount(request.getMinOrderAmount());
        }
        if (request.getDeliveryFee() != null) {
            restaurant.setDeliveryFee(request.getDeliveryFee());
        }
        if (request.getEstimatedDeliveryTime() != null) {
            restaurant.setEstimatedDeliveryTime(request.getEstimatedDeliveryTime());
        }
        if (request.getImageUrl() != null) {
            restaurant.setImageUrl(request.getImageUrl());
        }
        if (request.getBannerImageUrl() != null) {
            restaurant.setBannerImageUrl(request.getBannerImageUrl());
        }
        if (request.getOpeningHours() != null) {
            restaurant.setOpeningHours(request.getOpeningHours());
        }
        if (request.getIsOpen() != null) {
            restaurant.setIsOpen(request.getIsOpen());
        }
        if (request.getIsActive() != null) {
            restaurant.setIsActive(request.getIsActive());
        }

        restaurant = restaurantRepository.save(restaurant);

        log.info("Restaurant updated successfully: id={}", restaurant.getId());

        return mapToResponse(restaurant);
    }

    @Transactional
    public void deleteRestaurant(Long id) {
        log.info("Deleting restaurant: id={}", id);

        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Restaurant not found"));


        restaurant.setIsActive(false);
        restaurantRepository.save(restaurant);

        log.info("Restaurant deleted successfully: id={}", id);
    }

    @Transactional(readOnly = true)
    public boolean isOwner(Long restaurantId, Long userId) {
        Restaurant restaurant = restaurantRepository.findByIdWithCategory(restaurantId)
                .orElseThrow(() -> new NotFoundException("Restaurant not found"));

        return restaurant.getOwner().getId().equals(userId);
    }

    @Transactional
    public void updateRestaurantStatus(Long restaurantId, Long currentUserId, Boolean isOpen) {
        log.info("Updating restaurant status: restaurantId={}, userId={}, isOpen={}",
                restaurantId, currentUserId, isOpen);

        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new NotFoundException("Restaurant not found"));

        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        boolean isOwner = restaurant.getOwner().getId().equals(currentUserId);
        boolean isAdmin = currentUser.getRole().name().equals("ADMIN");

        if (!isOwner && !isAdmin) {
            log.warn("User {} is not owner or admin of restaurant {}", currentUserId, restaurantId);
            throw new com.utown.exception.ForbiddenException(
                    "You don't have permission to change this restaurant's status"
            );
        }

        restaurant.setIsOpen(isOpen);
        restaurantRepository.save(restaurant);

        log.info("Restaurant status updated successfully: id={}, isOpen={}", restaurantId, isOpen);
    }

    @Transactional(readOnly = true)
    public Page<RestaurantDto> getRestaurantsByOwnerId(Long ownerId, Pageable pageable) {
        log.info("Getting restaurants by ownerId={}", ownerId);

        Page<Restaurant> restaurants = restaurantRepository.findByOwnerId(ownerId, pageable);

        return restaurants.map(this::mapToResponse);
    }

    @Transactional(readOnly = true)
    public java.util.List<RestaurantDto> getRestaurantsByOwnerId(Long ownerId) {
        log.info("Getting all restaurants by ownerId={}", ownerId);

        java.util.List<Restaurant> restaurants = restaurantRepository.findByOwnerId(ownerId);

        return restaurants.stream()
                .map(this::mapToResponse)
                .collect(java.util.stream.Collectors.toList());
    }
    
    private RestaurantDto mapToResponse(Restaurant restaurant) {
        return RestaurantDto.builder()
                .id(restaurant.getId())
                .name(restaurant.getName())
                .description(restaurant.getDescription())
                .categoryId(restaurant.getCategory().getId())
                .categoryName(restaurant.getCategory().getName())
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
                .updatedAt(restaurant.getUpdatedAt())
                .build();
    }
}
