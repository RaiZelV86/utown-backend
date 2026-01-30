package com.utown.controller;

import com.utown.model.dto.ApiResponseDTO;
import com.utown.model.dto.address.AddressDTO;
import com.utown.model.dto.address.CreateAddressRequest;
import com.utown.model.dto.address.UpdateAddressRequest;
import com.utown.service.AddressService;
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
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/addresses")
@RequiredArgsConstructor
@Slf4j
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Addresses", description = "Endpoints for managing user addresses")
public class AddressController {

    private final AddressService addressService;

    @PostMapping
    @PreAuthorize("hasRole('CLIENT')")
    @Operation(
            summary = "Create a new address",
            description = "Create a new address for the authenticated user"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Address created successfully",
                    content = @Content(schema = @Schema(implementation = AddressDTO.class))
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
                    description = "Forbidden - requires CLIENT role"
            )
    })
    public ResponseEntity<ApiResponseDTO<AddressDTO>> createAddress(
            Authentication authentication,
            @Valid @RequestBody CreateAddressRequest request
    ) {
        Long userId = (Long) authentication.getPrincipal();
        AddressDTO address = addressService.createAddress(userId, request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDTO.success(address, "Address created successfully"));
    }

    @GetMapping
    @PreAuthorize("hasRole('CLIENT')")
    @Operation(
            summary = "Get all addresses",
            description = "Get all addresses for the authenticated user"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Addresses retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - invalid or missing token"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - requires CLIENT role"
            )
    })
    public ResponseEntity<ApiResponseDTO<List<AddressDTO>>> getAllAddresses(
            Authentication authentication
    ) {
        Long userId = (Long) authentication.getPrincipal();
        List<AddressDTO> addresses = addressService.getAllAddressesByUserId(userId);

        return ResponseEntity.ok(ApiResponseDTO.success(addresses));
    }

    @GetMapping("/default")
    @PreAuthorize("hasRole('CLIENT')")
    @Operation(
            summary = "Get default address",
            description = "Get the default address for the authenticated user"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Default address retrieved successfully",
                    content = @Content(schema = @Schema(implementation = AddressDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Default address not found"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - invalid or missing token"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - requires CLIENT role"
            )
    })
    public ResponseEntity<ApiResponseDTO<AddressDTO>> getDefaultAddress(
            Authentication authentication
    ) {
        Long userId = (Long) authentication.getPrincipal();
        AddressDTO address = addressService.getDefaultAddress(userId);

        return ResponseEntity.ok(ApiResponseDTO.success(address));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('CLIENT')")
    @Operation(
            summary = "Get address by ID",
            description = "Get a specific address by ID for the authenticated user"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Address retrieved successfully",
                    content = @Content(schema = @Schema(implementation = AddressDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Address not found"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - address does not belong to user"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - invalid or missing token"
            )
    })
    public ResponseEntity<ApiResponseDTO<AddressDTO>> getAddressById(
            Authentication authentication,
            @PathVariable Long id
    ) {
        Long userId = (Long) authentication.getPrincipal();
        AddressDTO address = addressService.getAddressById(id, userId);

        return ResponseEntity.ok(ApiResponseDTO.success(address));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('CLIENT')")
    @Operation(
            summary = "Update address",
            description = "Update an existing address for the authenticated user"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Address updated successfully",
                    content = @Content(schema = @Schema(implementation = AddressDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Address not found"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - address does not belong to user"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - invalid or missing token"
            )
    })
    public ResponseEntity<ApiResponseDTO<AddressDTO>> updateAddress(
            Authentication authentication,
            @PathVariable Long id,
            @Valid @RequestBody UpdateAddressRequest request
    ) {
        Long userId = (Long) authentication.getPrincipal();
        AddressDTO address = addressService.updateAddress(id, userId, request);

        return ResponseEntity.ok(ApiResponseDTO.success(address, "Address updated successfully"));
    }

    @PutMapping("/{id}/default")
    @PreAuthorize("hasRole('CLIENT')")
    @Operation(
            summary = "Set address as default",
            description = "Set an address as the default address for the authenticated user"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Address set as default successfully",
                    content = @Content(schema = @Schema(implementation = AddressDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Address not found"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - address does not belong to user"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - invalid or missing token"
            )
    })
    public ResponseEntity<ApiResponseDTO<AddressDTO>> setDefaultAddress(
            Authentication authentication,
            @PathVariable Long id
    ) {
        Long userId = (Long) authentication.getPrincipal();
        AddressDTO address = addressService.setDefaultAddress(id, userId);

        return ResponseEntity.ok(ApiResponseDTO.success(address, "Address set as default successfully"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('CLIENT')")
    @Operation(
            summary = "Delete address",
            description = "Delete an address for the authenticated user"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Address deleted successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Address not found"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - address does not belong to user"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - invalid or missing token"
            )
    })
    public ResponseEntity<ApiResponseDTO<Void>> deleteAddress(
            Authentication authentication,
            @PathVariable Long id
    ) {
        Long userId = (Long) authentication.getPrincipal();
        addressService.deleteAddress(id, userId);

        return ResponseEntity.ok(ApiResponseDTO.success("Address deleted successfully"));
    }
}




