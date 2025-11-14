package com.example.laptop_shop.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.laptop_shop.repository.entity.OrderEntity;

public interface OrderRepository extends JpaRepository<OrderEntity,Long> {

    //Tim kiem tat ca don hang cua mot nguoi dung cu the
    List<OrderEntity> findByUserId(Long userid);
    // Tim kiem theo trang thai (status)
    List<OrderEntity> findByStatus(String status);
    Page<OrderEntity> findAll(Pageable pageable);
}
