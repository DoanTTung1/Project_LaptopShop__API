// src/main/java/com/example/laptop_shop/service/addressservice/AddressService.java
package com.example.laptop_shop.service.addressservice;

import com.example.laptop_shop.model.AddressDTO;
import java.util.List;

public interface AddressService {
    List<AddressDTO> getMyAddresses();

    AddressDTO createAddress(AddressDTO addressDTO);

    void deleteAddress(Long addressId);
}