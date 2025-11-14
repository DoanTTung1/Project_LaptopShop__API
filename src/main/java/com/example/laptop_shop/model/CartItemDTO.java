package com.example.laptop_shop.model;

import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
public class CartItemDTO {
    private Long id;
    private Long productId;
    private String productName;
    private String productImageUrl;
    private Integer quantity;
    private BigDecimal priceAtAddition;
    private BigDecimal subTotal; // Tổng tiền cho mặt hàng này
}