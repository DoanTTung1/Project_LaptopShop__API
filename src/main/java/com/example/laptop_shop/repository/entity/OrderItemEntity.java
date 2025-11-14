package com.example.laptop_shop.repository.entity;

import java.math.BigDecimal;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import jakarta.persistence.*; 
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "order_items")
@Getter
@Setter
public class OrderItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "price", precision = 15, scale = 2)
    private BigDecimal price;
    
    // Khóa ngoại N-1 với OrderEntity
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id") // Bỏ thuộc tính onDelete ở đây
    @OnDelete(action = OnDeleteAction.CASCADE) // SỬA: Dùng annotation @OnDelete riêng biệt
    private OrderEntity order;

    // Khóa ngoại N-1 với ProductEntity
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id") // Bỏ thuộc tính onDelete ở đây
    @OnDelete(action = OnDeleteAction.CASCADE) // SỬA: Dùng annotation @OnDelete riêng biệt
    private ProductEntity product;
}