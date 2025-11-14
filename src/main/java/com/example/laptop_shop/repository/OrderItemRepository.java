package com.example.laptop_shop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.laptop_shop.repository.entity.OrderItemEntity;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItemEntity, Long> {
    
    // Tìm kiếm tất cả các mục (items) trong một đơn hàng cụ thể
    List<OrderItemEntity> findByOrderId(Long orderId);
    
    // Tìm kiếm tất cả các mục (items) của một sản phẩm cụ thể
    List<OrderItemEntity> findByProductId(Long productId);
}