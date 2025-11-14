package com.example.laptop_shop.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.laptop_shop.repository.entity.SupplierEntity;

public interface SupplierRepository extends JpaRepository<SupplierEntity,Long>{

    //Tim kiem Suplier theo name:
    Optional<SupplierEntity> findByName(String name);
    boolean existsByName(String name);
    boolean existsByPhone(String phone);
    boolean existsByEmail(String name);
}
