package com.utown.service;

import com.utown.exception.ForbiddenException;
import com.utown.exception.NotFoundException;
import com.utown.model.dto.restaurant.CreateMenuItemRequest;
import com.utown.model.dto.restaurant.CreateMenuItemOptionRequest;
import com.utown.model.dto.restaurant.MenuItemDTO;
import com.utown.model.dto.restaurant.RestaurantMenuDTO;
import com.utown.model.dto.restaurant.UpdateMenuItemRequest;
import com.utown.model.entity.MenuItem;
import com.utown.model.entity.MenuItemOption;
import com.utown.model.entity.Restaurant;
import com.utown.model.entity.User;
import com.utown.model.entity.mapper.MenuItemMapper;
import com.utown.model.enums.OptionType;
import com.utown.repository.MenuItemRepository;
import com.utown.repository.RestaurantRepository;
import com.utown.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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
    private final UserRepository userRepository;

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

    @Transactional
    public MenuItemDTO createMenuItem(CreateMenuItemRequest request, Long currentUserId) {
        log.info("Creating menu item: restaurantId={}, name={}, userId={}",
                request.getRestaurantId(), request.getName(), currentUserId);

        Restaurant restaurant = restaurantRepository.findById(request.getRestaurantId())
                .orElseThrow(() -> new NotFoundException("Restaurant not found"));

        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        boolean isOwner = restaurant.getOwner().getId().equals(currentUserId);
        boolean isAdmin = currentUser.getRole().name().equals("ADMIN");

        if (!isOwner && !isAdmin) {
            log.warn("User {} is not owner or admin of restaurant {}", currentUserId, restaurant.getId());
            throw new ForbiddenException("You don't have permission to add menu items to this restaurant");
        }

        MenuItem menuItem = MenuItem.builder()
                .restaurant(restaurant)
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .categoryName(request.getCategoryName())
                .imageUrl(request.getImageUrl())
                .isAvailable(request.getIsAvailable())
                .isSpicy(request.getIsSpicy())
                .spicyLevel(request.getSpicyLevel())
                .sortOrder(request.getSortOrder())
                .options(new ArrayList<>())
                .build();

        if (request.getOptions() != null && !request.getOptions().isEmpty()) {
            for (CreateMenuItemOptionRequest optionReq : request.getOptions()) {
                MenuItemOption option = MenuItemOption.builder()
                        .menuItem(menuItem)
                        .name(optionReq.getName())
                        .price(optionReq.getAdditionalPrice())
                        .type(OptionType.valueOf(optionReq.getOptionType().toUpperCase()))
                        .build();
                menuItem.getOptions().add(option);
            }
        }

        MenuItem saved = menuItemRepository.save(menuItem);
        log.info("Menu item created successfully: id={}", saved.getId());

        return MenuItemMapper.toDTO(saved);
    }

    @Transactional
    public MenuItemDTO updateMenuItem(Long id, UpdateMenuItemRequest request, Long currentUserId) {
        log.info("Updating menu item: id={}, userId={}", id, currentUserId);

        MenuItem menuItem = menuItemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Menu item not found"));

        Restaurant restaurant = menuItem.getRestaurant();

        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        boolean isOwner = restaurant.getOwner().getId().equals(currentUserId);
        boolean isAdmin = currentUser.getRole().name().equals("ADMIN");

        if (!isOwner && !isAdmin) {
            log.warn("User {} is not owner or admin of restaurant {}", currentUserId, restaurant.getId());
            throw new ForbiddenException("You don't have permission to update this menu item");
        }

        if (request.getName() != null) {
            menuItem.setName(request.getName());
        }
        if (request.getDescription() != null) {
            menuItem.setDescription(request.getDescription());
        }
        if (request.getPrice() != null) {
            menuItem.setPrice(request.getPrice());
        }
        if (request.getCategoryName() != null) {
            menuItem.setCategoryName(request.getCategoryName());
        }
        if (request.getImageUrl() != null) {
            menuItem.setImageUrl(request.getImageUrl());
        }
        if (request.getIsAvailable() != null) {
            menuItem.setIsAvailable(request.getIsAvailable());
        }
        if (request.getIsSpicy() != null) {
            menuItem.setIsSpicy(request.getIsSpicy());
        }
        if (request.getSpicyLevel() != null) {
            menuItem.setSpicyLevel(request.getSpicyLevel());
        }
        if (request.getSortOrder() != null) {
            menuItem.setSortOrder(request.getSortOrder());
        }

        if (request.getOptions() != null) {
            menuItem.getOptions().clear();
            for (CreateMenuItemOptionRequest optionReq : request.getOptions()) {
                MenuItemOption option = MenuItemOption.builder()
                        .menuItem(menuItem)
                        .name(optionReq.getName())
                        .price(optionReq.getAdditionalPrice())
                        .type(OptionType.valueOf(optionReq.getOptionType().toUpperCase()))
                        .build();
                menuItem.getOptions().add(option);
            }
        }

        MenuItem updated = menuItemRepository.save(menuItem);
        log.info("Menu item updated successfully: id={}", updated.getId());

        return MenuItemMapper.toDTO(updated);
    }

    @Transactional
    public void deleteMenuItem(Long id, Long currentUserId) {
        log.info("Deleting menu item: id={}, userId={}", id, currentUserId);

        MenuItem menuItem = menuItemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Menu item not found"));

        Restaurant restaurant = menuItem.getRestaurant();

        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        boolean isOwner = restaurant.getOwner().getId().equals(currentUserId);
        boolean isAdmin = currentUser.getRole().name().equals("ADMIN");

        if (!isOwner && !isAdmin) {
            log.warn("User {} is not owner or admin of restaurant {}", currentUserId, restaurant.getId());
            throw new ForbiddenException("You don't have permission to delete this menu item");
        }

        menuItemRepository.delete(menuItem);
        log.info("Menu item deleted successfully: id={}", id);
    }

}
