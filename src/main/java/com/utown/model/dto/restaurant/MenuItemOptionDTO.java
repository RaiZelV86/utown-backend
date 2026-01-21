package com.utown.model.dto.restaurant;

import com.utown.model.enums.OptionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuItemOptionDTO {

    private Long id;
    private String name;
    private BigDecimal price;
    private OptionType type;
    private String optionGroup;
    private Boolean isDefault;
}
