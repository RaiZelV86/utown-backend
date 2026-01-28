package com.utown.model.dto.address;

import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class UpdateAddressRequest {

    @Size(max = 500, message = "Address must not exceed 500 characters")
    private String address;

    @Size(max = 500, message = "Detail address must not exceed 500 characters")
    private String detailAddress;

    @Size(max = 100, message = "City must not exceed 100 characters")
    private String city;

    private BigDecimal latitude;

    private BigDecimal longitude;

    @Size(max = 1000, message = "Note must not exceed 1000 characters")
    private String note;

    @Size(max = 50, message = "Label must not exceed 50 characters")
    private String label;

    private Boolean isDefault;
}



