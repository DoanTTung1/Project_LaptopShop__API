package com.example.laptop_shop.model;

import lombok.Data;

@Data
public class JwtResponse {
    private String token;
    private String username;
    private String phone;
    private String email;
    private String role;
}
