package com.utown.controller;

import com.utown.model.dto.ApiResponseDTO;
import com.utown.model.dto.restaurant.CreateRestaurantRequest;
import com.utown.model.dto.restaurant.RestaurantDto;
import com.utown.model.dto.restaurant.UpdateRestaurantRequest;
import com.utown.service.RestaurantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/restaurants")
@RequiredArgsConstructor
@Slf4j
public class RestaurantController {

    private final RestaurantService restaurantService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDTO<RestaurantDto>> createRestaurant(
            @Valid @RequestBody CreateRestaurantRequest request,
            Authentication authentication
    ) {
        log.info("POST /api/restaurants - Creating restaurant");

        Long ownerId = (Long) authentication.getPrincipal();
        RestaurantDto response = restaurantService.createRestaurant(request, ownerId);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponseDTO.success(response, "Restaurant created successfully"));
    }

    @GetMapping
    public ResponseEntity<ApiResponseDTO<Page<RestaurantDto>>> getAllRestaurants(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") Sort.Direction direction
    ) {
        log.info("GET /api/restaurants - page={}, size={}", page, size);

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        Page<RestaurantDto> restaurants = restaurantService.getAllRestaurants(pageable);

        return ResponseEntity.ok(ApiResponseDTO.success(restaurants));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<RestaurantDto>> getRestaurantById(
            @PathVariable Long id
    ) {
        log.info("GET /api/restaurants/{} - Getting restaurant", id);

        RestaurantDto response = restaurantService.getRestaurantById(id);

        return ResponseEntity.ok(ApiResponseDTO.success(response));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @restaurantSecurity.isOwner(#id, principal)")
    public ResponseEntity<ApiResponseDTO<RestaurantDto>> updateRestaurant(
            @PathVariable Long id,
            @Valid @RequestBody UpdateRestaurantRequest request
    ) {
        log.info("PUT /api/restaurants/{} - Updating restaurant", id);

        RestaurantDto response = restaurantService.updateRestaurant(id, request);

        return ResponseEntity.ok(ApiResponseDTO.success(response, "Restaurant updated successfully"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDTO<Void>> deleteRestaurant(
            @PathVariable Long id
    ) {
        log.info("DELETE /api/restaurants/{} - Deleting restaurant", id);

        restaurantService.deleteRestaurant(id);

        return ResponseEntity.ok(ApiResponseDTO.success("Restaurant deleted successfully"));
    }
}
