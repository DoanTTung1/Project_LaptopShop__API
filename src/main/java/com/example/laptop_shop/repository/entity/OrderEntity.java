package com.example.laptop_shop.repository.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete; // Thêm import này
import org.hibernate.annotations.OnDeleteAction; // Thêm import này

@Entity
@Table(name = "orders")
@Getter
@Setter
public class OrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "total_price", precision = 15, scale = 2)
    private BigDecimal totalPrice;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", columnDefinition = "ENUM('PENDING','PAID','CANCELLED')")
    private OrderStatus status = OrderStatus.PENDING;
    @CreationTimestamp
    @Column(name = "created_at", columnDefinition = "DATETIME")
    private LocalDateTime createdAt;
    @Column(name = "shipping_address")
    private String shippingAddress;
    @Column(name = "phone", length = 15)
    private String phone;
    // Khóa ngoại N-1 với UserEntity
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private UserEntity user;

    // Mối quan hệ 1-N với OrderItemEntity
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItemEntity> orderItems;
}