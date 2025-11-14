package com.example.laptop_shop.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderRequestDTO {
    private String shippingAddress;
    private String paymentMethod;
    private String phone;
}