package com.example.laptop_shop.service.authservice;

import com.example.laptop_shop.model.ChangePasswordRequestDTO;
import com.example.laptop_shop.model.JwtResponse;
import com.example.laptop_shop.model.UserLoginDTO;
import com.example.laptop_shop.model.UsersRegisterDTO;

public interface AuthService {
    boolean register(UsersRegisterDTO registerDTO);
    JwtResponse login(UserLoginDTO loginDTO);
    void changePassword(ChangePasswordRequestDTO password);
}
