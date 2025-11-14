package com.example.laptop_shop.repository.entity;


import java.util.List;
import jakarta.persistence.*; 
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "suppliers")
@Getter
@Setter
public class SupplierEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 150)
    private String name;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "address")
    private String address;

    @Column(name = "email", length = 150)
    private String email;
    
    // Mối quan hệ 1-N với ProductEntity (Supplier cung cấp nhiều Products)
    @OneToMany(mappedBy = "supplier", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductEntity> products;
}