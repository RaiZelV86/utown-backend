package com.utown.security;

import com.utown.repository.RestaurantRepository;
import com.utown.service.RestaurantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component("restaurantSecurity")
@RequiredArgsConstructor
public class RestaurantSecurity {

    private final RestaurantService restaurantService;

    public boolean isOwner(Long restaurantId, Long userId) {
        return restaurantService.isOwner(restaurantId, userId);
    }
}
