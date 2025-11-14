package com.example.laptop_shop.model;

import com.example.laptop_shop.repository.entity.OrderStatus;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderUpdateStatusDTO {
    private OrderStatus status; 
}