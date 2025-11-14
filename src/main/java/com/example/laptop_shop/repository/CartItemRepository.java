package com.example.laptop_shop.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.laptop_shop.repository.entity.CartItemEntity;

public interface CartItemRepository extends JpaRepository<CartItemEntity, Long> {
    // Lấy toàn bộ sản phẩm trong giỏ
    List<CartItemEntity> findByCartId(Long cartId);

    // Kiểm tra 1 sản phẩm có trong giỏ chưa
    Optional<CartItemEntity> findByCart_IdAndProduct_Id(Long cartId, Long productId);

    // Xóa toàn bộ sản phẩm trgiỏ giỏ
    void deleteByCart_Id(Long cartId);
}
