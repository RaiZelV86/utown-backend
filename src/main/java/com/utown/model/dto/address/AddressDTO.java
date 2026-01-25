package com.utown.model.dto.address;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressDTO {

    private Long id;
    private Long userId;
    private String address;
    private String detailAddress;
    private String city;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String note;
    private String label;
    private Boolean isDefault;
    private LocalDateTime createdAt;
}
