package com.example.laptop_shop.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.example.laptop_shop.repository.entity.UserEntity;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long>, JpaSpecificationExecutor<UserEntity>{
    // Tìm kiếm user theo username
    Optional<UserEntity> findByUsername(String username);

    // Kiểm tra username đã tồn tại chưa
    boolean existsByUsername(String username);

    // Kiểm tra email đã tồn tại chưa
    boolean existsByEmail(String email);
    Long countByIdIn(List<Long> ids);
    void deleteByIdIn(List<Long>ids);
    boolean existsByPhone(String phone);
}
