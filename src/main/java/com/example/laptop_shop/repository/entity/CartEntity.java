package com.example.laptop_shop.repository.entity;

import jakarta.persistence.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime; // Thêm import
import java.util.List;

@Entity
@Table(name = "cart") // ĐÃ SỬA: Ánh xạ tới tên bảng 'cart'
@Getter
@Setter
public class CartEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Quan hệ 1-1 với User (Mỗi User có 1 Giỏ hàng). Dùng @MapsId nếu id là user_id.
    // Ở đây dùng @OneToOne và @JoinColumn vì ID là tự tăng (tương tự như SQL)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private UserEntity user;

    // Trường này KHÔNG CÓ trong SQL, nhưng cần thiết để tính toán tổng tiền
    @Transient // Không ánh xạ trường này vào DB, tính toán trong Service
    private BigDecimal totalPrice = BigDecimal.ZERO; 
    
    // Thêm các trường metadata từ SQL
    @Column(name = "created_at", insertable = false, updatable = false) // Mặc định trong SQL
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", insertable = false, updatable = false) // Mặc định trong SQL
    private LocalDateTime updatedAt;

    // Quan hệ 1-N với CartItem
    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItemEntity> items;
}
