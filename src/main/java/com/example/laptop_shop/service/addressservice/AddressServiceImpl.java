// src/main/java/com/example/laptop_shop/service/addressservice/AddressServiceImpl.java
package com.example.laptop_shop.service.addressservice;

import com.example.laptop_shop.model.AddressDTO;
import com.example.laptop_shop.repository.AddressRepository;
import com.example.laptop_shop.repository.UserRepository;
import com.example.laptop_shop.repository.entity.AddressEntity;
import com.example.laptop_shop.repository.entity.UserEntity;
import com.example.laptop_shop.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    private final SecurityUtils securityUtils;

    // Hàm helper lấy user hiện tại
    private UserEntity getCurrentUser() {
        Long userId = securityUtils.getCurrentId();
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy người dùng"));
    }

    // Hàm helper map Entity -> DTO
    private AddressDTO mapToDTO(AddressEntity entity) {
        AddressDTO dto = new AddressDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setAddress(entity.getAddress());
        dto.setPhone(entity.getPhone());
        return dto;
    }

    @Override
    public List<AddressDTO> getMyAddresses() {
        List<AddressDTO> dtos = new ArrayList<>();
        Long userId = getCurrentUser().getId();
        List<AddressEntity> addressEntities = addressRepository.findByUserId(userId);
        if (!addressEntities.isEmpty()) {
            for (int i = 0; i < addressEntities.size(); i++) {
                AddressEntity addressEntity = addressEntities.get(i);
                dtos.add(mapToDTO(addressEntity));
            }
            return dtos;
        } else
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy địa chỉ của người dùng !!");
    }

    @Override
    public AddressDTO createAddress(AddressDTO addressDTO) {
        UserEntity currentUser = getCurrentUser();

        AddressEntity newAddress = new AddressEntity();
        newAddress.setUser(currentUser);
        newAddress.setName(addressDTO.getName());
        newAddress.setAddress(addressDTO.getAddress());
        newAddress.setPhone(addressDTO.getPhone());

        AddressEntity savedAddress = addressRepository.save(newAddress);
        return mapToDTO(savedAddress);
    }

    @Override
    public void deleteAddress(Long addressId) {
        Long currentUserId = getCurrentUser().getId();

        AddressEntity address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy địa chỉ"));

        // Kiểm tra xem địa chỉ này có thuộc về user đang đăng nhập không
        if (!address.getUser().getId().equals(currentUserId)) {
            throw new AccessDeniedException("Bạn không có quyền xóa địa chỉ này.");
        }

        addressRepository.delete(address);
    }
}