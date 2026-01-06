package com.utown.controller;

import com.utown.model.dto.ApiResponseDTO;
import com.utown.model.dto.restaurant.CreateRestaurantRequest;
import com.utown.model.dto.restaurant.RestaurantDto;
import com.utown.model.dto.restaurant.UpdateRestaurantRequest;
import com.utown.service.RestaurantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Restaurants", description = "Restaurant management endpoints")
public class RestaurantController {

    private final RestaurantService restaurantService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "Create restaurant (Admin only)",
            description = "Create a new restaurant. Requires ADMIN role."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Restaurant created successfully",
                    content = @Content(schema = @Schema(implementation = RestaurantDto.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - invalid or missing token"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - requires ADMIN role"
            )
    })
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
    @Operation(
            summary = "Get all restaurants",
            description = "Retrieve paginated list of all restaurants (public endpoint)"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Restaurants retrieved successfully"
            )
    })
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
    @Operation(
            summary = "Get restaurant by ID",
            description = "Retrieve restaurant details by ID (public endpoint)"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Restaurant retrieved successfully",
                    content = @Content(schema = @Schema(implementation = RestaurantDto.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Restaurant not found"
            )
    })
    public ResponseEntity<ApiResponseDTO<RestaurantDto>> getRestaurantById(
            @PathVariable Long id
    ) {
        log.info("GET /api/restaurants/{} - Getting restaurant", id);

        RestaurantDto response = restaurantService.getRestaurantById(id);

        return ResponseEntity.ok(ApiResponseDTO.success(response));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @restaurantSecurity.isOwner(#id, principal)")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "Update restaurant",
            description = "Update restaurant details. Requires ADMIN role or restaurant owner."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Restaurant updated successfully",
                    content = @Content(schema = @Schema(implementation = RestaurantDto.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - invalid or missing token"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - not owner or admin"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Restaurant not found"
            )
    })
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
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "Delete restaurant (Admin only)",
            description = "Delete restaurant by ID. Requires ADMIN role."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Restaurant deleted successfully"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - invalid or missing token"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - requires ADMIN role"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Restaurant not found"
            )
    })
    public ResponseEntity<ApiResponseDTO<Void>> deleteRestaurant(
            @PathVariable Long id
    ) {
        log.info("DELETE /api/restaurants/{} - Deleting restaurant", id);

        restaurantService.deleteRestaurant(id);

        return ResponseEntity.ok(ApiResponseDTO.success("Restaurant deleted successfully"));
    }
}
