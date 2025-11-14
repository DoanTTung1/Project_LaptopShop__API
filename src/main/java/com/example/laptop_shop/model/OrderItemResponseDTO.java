package com.example.laptop_shop.model;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderItemResponseDTO {
    private Long id;
    private Long productId;
    private String productName;
    private int quantity;
    private BigDecimal priceAtPurchase;
    private String productImageUrl;
}