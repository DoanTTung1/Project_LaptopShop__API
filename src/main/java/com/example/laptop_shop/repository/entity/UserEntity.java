package com.example.laptop_shop.repository.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", nullable = false, unique = true, length = 100)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;
    @Column(name = "phone", length = 15)
    private String phone;
    @Column(name = "email", length = 150)
    private String email;

    @Enumerated(EnumType.STRING) // Ánh xạ ENUM sang String trong DB
    @Column(name = "role", columnDefinition = "ENUM('USER', 'ADMIN')")
    private Role role = Role.USER;

    @Column(name = "created_at", columnDefinition = "DATETIME")
    private LocalDateTime createdAt;

    // Mối quan hệ 1-N với OrderEntity
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderEntity> orders;
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference // Dùng JsonManagedReference/JsonBackReference hoặc @JsonIgnore để tránh lặp vô
                          // hạn
    private List<AddressEntity> addresses = new ArrayList<>();
}