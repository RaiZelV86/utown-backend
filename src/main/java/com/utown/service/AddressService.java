package com.utown.service;

import com.utown.exception.ForbiddenException;
import com.utown.exception.NotFoundException;
import com.utown.model.dto.address.AddressDTO;
import com.utown.model.dto.address.CreateAddressRequest;
import com.utown.model.dto.address.UpdateAddressRequest;
import com.utown.model.entity.Address;
import com.utown.model.entity.User;
import com.utown.model.entity.mapper.AddressMapper;
import com.utown.repository.AddressRepository;
import com.utown.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    private final AddressMapper addressMapper;

    @Transactional
    public AddressDTO createAddress(Long userId, CreateAddressRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        if (Boolean.TRUE.equals(request.getIsDefault())) {
            addressRepository.findByUserIdAndIsDefaultTrue(userId)
                    .ifPresent(existingDefault -> {
                        existingDefault.setIsDefault(false);
                        addressRepository.save(existingDefault);
                    });
        }

        Address address = Address.builder()
                .user(user)
                .address(request.getAddress())
                .detailAddress(request.getDetailAddress())
                .city(request.getCity())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .note(request.getNote())
                .label(request.getLabel())
                .isDefault(request.getIsDefault() != null ? request.getIsDefault() : false)
                .build();

        address = addressRepository.save(address);
        log.info("Address created: addressId={}, userId={}", address.getId(), userId);

        return addressMapper.toDto(address);
    }

    public AddressDTO getAddressById(Long addressId, Long userId) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new NotFoundException("Address not found"));

        if (!address.getUser().getId().equals(userId)) {
            throw new ForbiddenException("Address does not belong to user");
        }

        return addressMapper.toDto(address);
    }

    public List<AddressDTO> getAllAddressesByUserId(Long userId) {
        List<Address> addresses = addressRepository.findByUserId(userId);
        return addresses.stream()
                .map(addressMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public AddressDTO updateAddress(Long addressId, Long userId, UpdateAddressRequest request) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new NotFoundException("Address not found"));

        if (!address.getUser().getId().equals(userId)) {
            throw new ForbiddenException("Address does not belong to user");
        }

        if (request.getAddress() != null) {
            address.setAddress(request.getAddress());
        }
        if (request.getDetailAddress() != null) {
            address.setDetailAddress(request.getDetailAddress());
        }
        if (request.getCity() != null) {
            address.setCity(request.getCity());
        }
        if (request.getLatitude() != null) {
            address.setLatitude(request.getLatitude());
        }
        if (request.getLongitude() != null) {
            address.setLongitude(request.getLongitude());
        }
        if (request.getNote() != null) {
            address.setNote(request.getNote());
        }
        if (request.getLabel() != null) {
            address.setLabel(request.getLabel());
        }
        if (request.getIsDefault() != null) {
            if (Boolean.TRUE.equals(request.getIsDefault())) {
                addressRepository.findByUserIdAndIsDefaultTrue(userId)
                        .ifPresent(existingDefault -> {
                            if (!existingDefault.getId().equals(addressId)) {
                                existingDefault.setIsDefault(false);
                                addressRepository.save(existingDefault);
                            }
                        });
            }
            address.setIsDefault(request.getIsDefault());
        }

        address = addressRepository.save(address);
        log.info("Address updated: addressId={}, userId={}", addressId, userId);

        return addressMapper.toDto(address);
    }

    @Transactional
    public void deleteAddress(Long addressId, Long userId) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new NotFoundException("Address not found"));

        if (!address.getUser().getId().equals(userId)) {
            throw new ForbiddenException("Address does not belong to user");
        }

        addressRepository.delete(address);
        log.info("Address deleted: addressId={}, userId={}", addressId, userId);
    }

    @Transactional
    public AddressDTO setDefaultAddress(Long addressId, Long userId) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new NotFoundException("Address not found"));

        if (!address.getUser().getId().equals(userId)) {
            throw new ForbiddenException("Address does not belong to user");
        }

        addressRepository.findByUserIdAndIsDefaultTrue(userId)
                .ifPresent(existingDefault -> {
                    if (!existingDefault.getId().equals(addressId)) {
                        existingDefault.setIsDefault(false);
                        addressRepository.save(existingDefault);
                    }
                });

        address.setIsDefault(true);
        address = addressRepository.save(address);
        log.info("Address set as default: addressId={}, userId={}", addressId, userId);

        return addressMapper.toDto(address);
    }

    public AddressDTO getDefaultAddress(Long userId) {
        Address address = addressRepository.findByUserIdAndIsDefaultTrue(userId)
                .orElseThrow(() -> new NotFoundException("Default address not found"));

        return addressMapper.toDto(address);
    }
}

