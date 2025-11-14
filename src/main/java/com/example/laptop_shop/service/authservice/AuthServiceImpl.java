package com.example.laptop_shop.service.authservice;

import java.time.LocalDateTime;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.laptop_shop.model.ChangePasswordRequestDTO;
import com.example.laptop_shop.model.JwtResponse;
import com.example.laptop_shop.model.UserLoginDTO;
import com.example.laptop_shop.model.UsersRegisterDTO;
import com.example.laptop_shop.repository.UserRepository;
import com.example.laptop_shop.repository.entity.Role;
import com.example.laptop_shop.repository.entity.UserEntity;
import com.example.laptop_shop.security.SecurityUtils;
import com.example.laptop_shop.security.jwt.JwtCore;
import com.example.laptop_shop.service.cartservice.CartService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtCore jwtCore;
    private final UserDetailsService userDetailsService;
    private final SecurityUtils security;
    private final CartService cartService;

    @Override
    public JwtResponse login(UserLoginDTO loginDTO) {
        // BƯỚC 1: LẤY THÔNG TIN USER TỪ DATABASE
        UserDetails userDetails;
        try {
            userDetails = userDetailsService.loadUserByUsername(loginDTO.getUsername());
        } catch (UsernameNotFoundException e) {
            throw new BadCredentialsException("Thông tin đăng nhập không chính xác");
        }

        // BƯỚC 2: KIỂM TRA MẬT KHẨU BẰNG TAY
        if (!passwordEncoder.matches(loginDTO.getPassword(), userDetails.getPassword())) {
            throw new BadCredentialsException("Thông tin đăng nhập không chính xác");
        }

        // BƯỚC 3: NẾU MẬT KHẨU KHỚP, TỰ TẠO ĐỐI TƯỢNG AUTHENTICATION
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null,
                userDetails.getAuthorities());

        // BƯỚC 4: LƯU VÀO SECURITY CONTEXT
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // BƯỚC 5: TẠO TOKEN VÀ TRẢ VỀ RESPONSE
        UserEntity userEntity = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new BadCredentialsException("Không tìm thấy người dùng"));

        String role = "ROLE_" + userEntity.getRole().name();
        String jwtToken = jwtCore.generateToken(userDetails.getUsername(), role);
        JwtResponse jwtResponse = new JwtResponse();
        jwtResponse.setEmail(userEntity.getEmail());
        jwtResponse.setUsername(userEntity.getUsername());
        jwtResponse.setPhone(userEntity.getPhone());
        jwtResponse.setToken(jwtToken);
        jwtResponse.setRole(userEntity.getRole().name());
        return jwtResponse;
    }

    @Override
    public boolean register(UsersRegisterDTO registerDTO) {
        if (userRepository.existsByUsername(registerDTO.getUsername()))
            return false;
        if (userRepository.existsByEmail(registerDTO.getEmail()))
            return false;
        if (userRepository.existsByPhone(registerDTO.getPhone()))
            return false;
        UserEntity u = new UserEntity();
        u.setUsername(registerDTO.getUsername());
        u.setEmail(registerDTO.getEmail());
        u.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
        u.setPhone(registerDTO.getPhone());
        u.setRole(Role.USER);
        u.setCreatedAt(LocalDateTime.now());
        UserEntity result = userRepository.save(u);
        cartService.layHoacTaoGioHang(result.getId());
        return true;
    }

    @Override
    public void changePassword(ChangePasswordRequestDTO password) {
        if (!password.getNewPassword().equals(password.getConfirmationPassword())) {
            throw new RuntimeException("Mật khẩu mới và mật khẩu xác nhận không khớp nhau");
        }
        Long userId = security.getCurrentId();
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Không tìm thấy người dùng có id là: " + userId));
        if (!passwordEncoder.matches(password.getCurrentPassword(), userEntity.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mật khẩu hiện tại không chính xác");
        }
        String encodeNewPassword = passwordEncoder.encode(password.getNewPassword());
        userEntity.setPassword(encodeNewPassword);
        userRepository.save(userEntity);
    }

}
