// src/main/java/com/example/laptop_shop/controller/AddressController.java
package com.example.laptop_shop.controller;

import com.example.laptop_shop.model.AddressDTO;
import com.example.laptop_shop.service.addressservice.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/addresses")
@RequiredArgsConstructor
@PreAuthorize("hasRole('USER')") 
public class AddressController {

    private final AddressService addressService;

 
    @GetMapping("/my")
    public ResponseEntity<List<AddressDTO>> getMyAddresses() {
        return ResponseEntity.ok(addressService.getMyAddresses());
    }
    @PostMapping
    public ResponseEntity<AddressDTO> createAddress(@RequestBody AddressDTO addressDTO) {
        AddressDTO newAddress = addressService.createAddress(addressDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(newAddress);
    }

    // API để xóa
    // DELETE /api/addresses/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAddress(@PathVariable Long id) {
        addressService.deleteAddress(id);
        return ResponseEntity.noContent().build(); 
    }
}