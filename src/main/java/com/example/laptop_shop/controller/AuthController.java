package com.example.laptop_shop.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.laptop_shop.model.ChangePasswordRequestDTO;
import com.example.laptop_shop.model.JwtResponse;
import com.example.laptop_shop.model.UserLoginDTO;
import com.example.laptop_shop.model.UsersRegisterDTO;
import com.example.laptop_shop.service.authservice.AuthService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<String> dangKyThanhVien(@RequestBody UsersRegisterDTO user) {
        if ((user.getUsername() == null || user.getUsername().trim().isEmpty())
                || (user.getPassword() == null || user.getPassword().trim().isEmpty())
                || (user.getEmail() == null || user.getEmail().trim().isEmpty())) {
            return ResponseEntity.badRequest().body("Vui lòng điền đầy đủ thông tin để đăng kí");
        }
        if (authService.register(user))
            return ResponseEntity.ok("Đăng kí thành công");
        else
            return ResponseEntity.badRequest().body("Error: Tên người dùng hoặc Email đã được sử dụng!");
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> authenticateUser(@RequestBody UserLoginDTO loginDTO) {
        JwtResponse response = authService.login(loginDTO);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/changepassword")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequestDTO password) {
        if (password.getConfirmationPassword() == null || password.getCurrentPassword() == null
                || password.getNewPassword() == null || password.getConfirmationPassword().trim().isEmpty()
                || password.getCurrentPassword().trim().isEmpty() || password.getNewPassword().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Vui lòng nhập đầy đủ thông tin mật khẩu");
        }

        authService.changePassword(password);
        return ResponseEntity.ok().body("Cập nhật mật khẩu thành công");
    }
}
