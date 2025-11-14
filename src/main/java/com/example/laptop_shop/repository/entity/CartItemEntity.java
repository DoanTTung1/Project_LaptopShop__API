package com.example.laptop_shop.repository.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime; // Cần thiết cho created_at và updated_at

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "cart_item") // ĐÃ SỬA: Ánh xạ tới tên bảng 'cart_item'
@Getter
@Setter
public class CartItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "price", precision = 10, scale = 2, nullable = false) // DECIMAL(10,2) trong SQL
    private BigDecimal price;

    // Khóa ngoại N-1 với CartEntity
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", nullable = false)
    private CartEntity cart;

    // Khóa ngoại N-1 với ProductEntity
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private ProductEntity product;

    // Thêm các trường metadata từ SQL
    @CreationTimestamp
    @Column(name = "created_at", insertable = false, updatable = false) // Mặc định trong SQL
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", insertable = false, updatable = false) // Mặc định trong SQL
    private LocalDateTime updatedAt;
}
