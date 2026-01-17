package com.utown.service;

import com.utown.exception.NotFoundException;
import com.utown.model.dto.restaurant.MenuItemDTO;
import com.utown.model.dto.restaurant.RestaurantMenuDTO;
import com.utown.model.entity.MenuItem;
import com.utown.model.entity.Restaurant;
import com.utown.model.entity.mapper.MenuItemMapper;
import com.utown.repository.MenuItemRepository;
import com.utown.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MenuItemService {

    private final MenuItemRepository menuItemRepository;
    private final RestaurantRepository restaurantRepository;

    @Transactional
    public RestaurantMenuDTO getRestaurantMenu(Long restaurantId, String categoryName, boolean isAvailable) {
        log.debug("Getting restaurant menu: {}, category: {}, aviable: {} ",
                restaurantId, categoryName, isAvailable);

        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new IllegalArgumentException("Restaurant not found with id: " + restaurantId));

        List<MenuItem> menuItems = menuItemRepository.findByRestaurantWithFilters(
                restaurantId,
                categoryName,
                isAvailable
        );

        Map<String, List<MenuItemDTO>> menuByCategory = menuItems.stream()
                .map(MenuItemMapper::toDTO)
                .collect(Collectors.groupingBy(
                       item -> item.getCategoryName() != null ? item.getCategoryName() : "Order",
                        LinkedHashMap::new,
                        Collectors.toList()
                ));

        return RestaurantMenuDTO.builder()
                .restaurantId(restaurant.getId())
                .restaurantName(restaurant.getName())
                .isOpen(restaurant.getIsOpen())
                .menuByCategory(menuByCategory)
                .build();
    }

    @Transactional(readOnly = true)
    public List<MenuItemDTO> getRestaurantMenuItems(Long restaurantId) {
        log.debug("Getting all menu items for restaurant: {}", restaurantId);

        // Проверяем существование ресторана
        if (!restaurantRepository.existsById(restaurantId)) {
            throw new NotFoundException("Restaurant not found with id: " + restaurantId);
        }

        List<MenuItem> menuItems = menuItemRepository
                .findByRestaurantIdOrderBySortOrderAscCreatedAtDesc(restaurantId);

        return MenuItemMapper.toDTOList(menuItems);
    }

    @Transactional(readOnly = true)
    public MenuItemDTO getMenuItem(Long id) {
        log.debug("Getting menu item: {}", id);

        MenuItem menuItem = menuItemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Menu item not found with id: " + id));

        return MenuItemMapper.toDTO(menuItem);
    }

}
