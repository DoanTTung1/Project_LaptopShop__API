package com.example.laptop_shop.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.laptop_shop.repository.entity.CategoryEntity;

public interface CategoryRepository extends JpaRepository<CategoryEntity,Long> {
    //Tìm kiếm Category theo name
    Optional<CategoryEntity> findByNameIgnoreCase(String name);
}
