package com.example.laptop_shop.service.cartservice;

import java.math.BigDecimal;
import java.util.List;

import com.example.laptop_shop.model.CartDTO;
import com.example.laptop_shop.repository.entity.CartEntity;
import com.example.laptop_shop.repository.entity.ProductEntity;

public interface CartService {
    // Quản lý giỏ hàng
    CartEntity layHoacTaoGioHang(Long userId);

    // Quản lý sản phẩm trong giỏ
    void themSanPhamVaoGio(ProductEntity product, int quantity);

    void capNhatSoLuong(ProductEntity product, int newQuantity);

    void xoaMotSanPhamTrongGio(Long productId);

    void xoaNhieuSanPhamTrongGio(List<Long> CartItemIds);

    // Tính toán
    BigDecimal tinhTongTienGioHang(CartEntity cart);

    int demSoSanPhamTrongGio();

    // Xem giỏ hàng
    CartDTO xemGioHang();

}