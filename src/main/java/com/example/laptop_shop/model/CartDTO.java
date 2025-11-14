package com.example.laptop_shop.model;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartDTO {

    private Long cartId;                  // id giỏ hàng
    private Long userId;                  // id người dùng
    private List<CartItemDTO> items;      // danh sách sản phẩm
    private BigDecimal totalAmount;       // tổng tiền tất cả sản phẩm
    private int totalItems;               // tổng số lượng sản phẩm (đếm)
}
