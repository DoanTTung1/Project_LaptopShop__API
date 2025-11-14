package com.example.laptop_shop.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateCartQuantityRequest {
    private Long productId;
    private int newQuantity;
}
