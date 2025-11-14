// src/main/java/com/example/laptop_shop/repository/AddressRepository.java
package com.example.laptop_shop.repository;

import com.example.laptop_shop.repository.entity.AddressEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddressRepository extends JpaRepository<AddressEntity, Long> {
    // Tự động tạo câu lệnh SELECT * FROM addresses WHERE user_id = ?
    List<AddressEntity> findByUserId(Long userId);
}