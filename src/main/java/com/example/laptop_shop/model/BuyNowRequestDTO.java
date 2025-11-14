package com.example.laptop_shop.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BuyNowRequestDTO {

    private Long productId;
    private int quantity;
    private String shippingAddress;
    private String paymentMethod;
    private String phone;
}