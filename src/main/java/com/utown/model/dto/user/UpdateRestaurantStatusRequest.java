package com.utown.model.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to change restaurant status")
public class UpdateRestaurantStatusRequest {

    @NotNull(message = "isOpen is required")
    @Schema(description = "Статус ресторана (открыт/закрыт)", example = "true")
    private Boolean isOpen;
}
