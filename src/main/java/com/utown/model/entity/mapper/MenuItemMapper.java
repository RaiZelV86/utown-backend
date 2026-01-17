package com.utown.model.entity.mapper;

import com.utown.model.dto.restaurant.MenuItemDTO;
import com.utown.model.dto.restaurant.MenuItemOptionDTO;
import com.utown.model.entity.MenuItem;
import com.utown.model.entity.MenuItemOption;

import java.util.List;
import java.util.stream.Collectors;

public class MenuItemMapper {

    public static MenuItemDTO toDTO(MenuItem entity) {
        if (entity == null) {
            return null;
        }

        return MenuItemDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .price(entity.getPrice())
                .imageUrl(entity.getImageUrl())
                .isAvailable(entity.getIsAvailable())
                .isSpicy(entity.getIsSpicy())
                .spicyLevel(entity.getSpicyLevel())
                .categoryName(entity.getCategoryName())
                .options(toOptionDTOList(entity.getOptions()))
                .build();
    }

    public static List<MenuItemDTO> toDTOList(List<MenuItem> entities) {
        if (entities == null) {
            return null;
        }

        return entities.stream()
                .map(MenuItemMapper::toDTO)
                .collect(Collectors.toList());
    }

    public static MenuItemOptionDTO toOptionDTO(MenuItemOption entity) {
        if (entity == null) {
            return null;
        }

        return MenuItemOptionDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .price(entity.getPrice())
                .type(entity.getType())
                .optionGroup(entity.getOptionGroup())
                .isDefault(entity.getIsDefault())
                .build();
    }

    public static List<MenuItemOptionDTO> toOptionDTOList(List<MenuItemOption> entities) {
        if (entities == null || entities.isEmpty()) {
            return List.of();
        }

        return entities.stream()
                .map(MenuItemMapper::toOptionDTO)
                .collect(Collectors.toList());
    }
}
