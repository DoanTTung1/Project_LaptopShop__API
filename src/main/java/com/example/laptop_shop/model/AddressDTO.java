// src/main/java/com/example/laptop_shop/model/AddressDTO.java
package com.example.laptop_shop.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddressDTO {
    private Long id; // Dùng khi trả về

    private String name;

    private String address;

    private String phone;
}