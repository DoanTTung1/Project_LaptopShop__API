// src/main/java/com/example/laptop_shop/repository/entity/AddressEntity.java
package com.example.laptop_shop.repository.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "addresses")
@Getter
@Setter
public class AddressEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Liên kết với người dùng. Một người dùng có thể có nhiều địa chỉ.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(nullable = false, length = 100)
    private String name; // Tên địa chỉ (Vd: "Nhà riêng", "Văn phòng")

    @Column(nullable = false)
    private String address; // Địa chỉ chi tiết

    @Column(nullable = false, length = 15)
    private String phone; // Số điện thoại
}