package com.utown.model.dto.restaurant;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateCategoryRequest {

    @NotBlank(message = "Name is required")
    private String name;

    private String iconUrl;
    private Integer priority;
}
