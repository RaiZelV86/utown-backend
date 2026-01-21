package com.utown.model.dto.restaurant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantMenuDTO {

    private Long restaurantId;
    private String restaurantName;
    private Boolean isOpen;
    private Map<String, List<MenuItemDTO>> menuByCategory;
}
