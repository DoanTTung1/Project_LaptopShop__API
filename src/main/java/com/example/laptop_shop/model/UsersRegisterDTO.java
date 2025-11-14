package com.example.laptop_shop.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UsersRegisterDTO {
    private String username;
    private String password;
    private String email;
    private String phone;
}
