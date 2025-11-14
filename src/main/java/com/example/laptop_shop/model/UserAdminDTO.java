package com.example.laptop_shop.model;

import java.time.LocalDateTime;

import com.example.laptop_shop.repository.entity.Role;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserAdminDTO {
    private Long id;
    private String username;
    private String email;
    private Role role;
    private LocalDateTime created_at;
}
