package com.example.laptop_shop.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.laptop_shop.model.AddToCartRequest;
import com.example.laptop_shop.model.UpdateCartQuantityRequest;
import com.example.laptop_shop.repository.ProductRepository;
import com.example.laptop_shop.repository.entity.ProductEntity;
import com.example.laptop_shop.security.SecurityUtils;
import com.example.laptop_shop.service.cartservice.CartService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cart")
@PreAuthorize("hasRole('USER')")
public class CartController {
    private final CartService cartService;
    private final ProductRepository productRepository;
    private final SecurityUtils securityUtils;

    @GetMapping
    public ResponseEntity<?> xemGioHang() {
        return ResponseEntity.ok().body(cartService.xemGioHang());
    }

    @PostMapping("/add")
    public ResponseEntity<?> themSanPhamVaoGioHang(@RequestBody AddToCartRequest request) {
        ProductEntity productEntity = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy sản phẩm !!"));
        cartService.themSanPhamVaoGio(productEntity, request.getQuantity());
        return ResponseEntity.ok().body("Thêm sản phẩm vào giỏ thành công !!");
    }

    @PutMapping("/update")
    public ResponseEntity<String> capNhatSoLuong(@RequestBody UpdateCartQuantityRequest request) {
        ProductEntity product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm !!"));

        cartService.capNhatSoLuong(product, request.getNewQuantity());
        return ResponseEntity.ok("Cập nhật số lượng thành công!");
    }

    @DeleteMapping("/remove/{productId}")
    public ResponseEntity<String> xoaMotSanPham(@PathVariable Long productId) {
        cartService.xoaMotSanPhamTrongGio(productId);
        return ResponseEntity.ok("Đã xóa sản phẩm khỏi giỏ hàng!");
    }

    @DeleteMapping("/remove-many")
    public ResponseEntity<String> xoaNhieuSanPham(@RequestBody List<Long> cartItemIds) {
        cartService.xoaNhieuSanPhamTrongGio(cartItemIds);
        return ResponseEntity.ok("Đã xóa các sản phẩm được chọn khỏi giỏ hàng!");
    }

    @GetMapping("/count")
    public ResponseEntity<Integer> demSoSanPham() {
        int count = cartService.demSoSanPhamTrongGio();
        return ResponseEntity.ok(count);
    }

    @GetMapping("/total")
    public ResponseEntity<BigDecimal> tinhTongTien() {
        Long userId = securityUtils.getCurrentId();
        BigDecimal tongTien = cartService.tinhTongTienGioHang(cartService.layHoacTaoGioHang(userId));
        return ResponseEntity.ok(tongTien);
    }
}
