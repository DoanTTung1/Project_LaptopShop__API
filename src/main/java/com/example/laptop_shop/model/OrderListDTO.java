package com.example.laptop_shop.model;

// import com.example.laptop_shop.entity.OrderStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.example.laptop_shop.repository.entity.OrderStatus;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderListDTO {
    private Long id;
    private OrderStatus status;
    private BigDecimal totalPrice;
    private LocalDateTime createdAt;
    private int totalItems;
}