package com.utown.service;

import com.utown.model.dto.restaurant.CategoryDTO;
import com.utown.model.dto.restaurant.CreateCategoryRequest;
import com.utown.model.entity.Category;
import com.utown.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Transactional
    public CategoryDTO createCategory(CreateCategoryRequest request) {
        Category category = Category.builder()
                .name(request.getName())
                .iconUrl(request.getIconUrl())
                .priority(request.getPriority() != null ? request.getPriority() : 0)
                .isActive(true)
                .build();

        Category saved = categoryRepository.save(category);

        return toDTO(saved);
    }

    @Transactional(readOnly = true)
    public List<CategoryDTO> getAllActiveCategories() {
        List<Category> categories = categoryRepository.findByIsActiveTrueOrderByPriorityAsc();
        return categories.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    private CategoryDTO toDTO(Category entity) {
        return CategoryDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .iconUrl(entity.getIconUrl())
                .priority(entity.getPriority())
                .isActive(entity.getIsActive())
                .build();
    }
}
