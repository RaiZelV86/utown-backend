package com.utown.model.entity.mapper;

import com.utown.model.dto.address.AddressDTO;
import com.utown.model.entity.Address;
import org.springframework.stereotype.Component;

@Component
public class AddressMapper {

    public AddressDTO toDto(Address address) {
        if (address == null) {
            return null;
        }

        return AddressDTO.builder()
                .id(address.getId())
                .userId(address.getUser() != null ? address.getUser().getId() : null)
                .address(address.getAddress())
                .detailAddress(address.getDetailAddress())
                .city(address.getCity())
                .latitude(address.getLatitude())
                .longitude(address.getLongitude())
                .note(address.getNote())
                .label(address.getLabel())
                .isDefault(address.getIsDefault())
                .createdAt(address.getCreatedAt())
                .build();
    }
}

