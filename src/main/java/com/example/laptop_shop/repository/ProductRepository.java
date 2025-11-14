package com.example.laptop_shop.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import org.springframework.stereotype.Repository;

import com.example.laptop_shop.repository.entity.ProductEntity;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Long>, JpaSpecificationExecutor<ProductEntity> {
    Optional<ProductEntity> findByImageUrl(String name);

    List<ProductEntity> findBySupplierId(Long supplierId);

    long countByIdIn(List<Long> ids);

    List<ProductEntity> findAllByIdIn(List<Long> ids);

    Page<ProductEntity> findByActive(boolean active, Pageable pageable);

    List<ProductEntity> findByActive(boolean active);
}
