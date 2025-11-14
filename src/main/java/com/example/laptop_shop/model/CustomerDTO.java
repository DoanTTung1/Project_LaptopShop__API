package com.example.laptop_shop.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerDTO {
    private Long userId;
    private String username;
    private String email;
    private String phone;
}