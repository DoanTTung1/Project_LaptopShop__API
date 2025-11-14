package com.example.laptop_shop.service.cartservice;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.laptop_shop.model.CartDTO;
import com.example.laptop_shop.model.CartItemDTO;
import com.example.laptop_shop.repository.CartItemRepository;
import com.example.laptop_shop.repository.CartRepository;
import com.example.laptop_shop.repository.UserRepository;
import com.example.laptop_shop.repository.entity.CartEntity;
import com.example.laptop_shop.repository.entity.CartItemEntity;
import com.example.laptop_shop.repository.entity.ProductEntity;
import com.example.laptop_shop.repository.entity.UserEntity;
import com.example.laptop_shop.security.SecurityUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final SecurityUtils securityUtils;

    private CartDTO toCartDTO(CartEntity entity) {
        List<CartItemEntity> items = entity.getItems();
        List<CartItemDTO> itemDTOs = new ArrayList<>();

        BigDecimal totalAmount = BigDecimal.ZERO;
        int totalItems = 0;

        for (int i = 0; i < items.size(); i++) {
            CartItemEntity itemEntity = items.get(i);

            BigDecimal subtotal = itemEntity.getPrice()
                    .multiply(BigDecimal.valueOf(itemEntity.getQuantity()));

            CartItemDTO itemDTO = new CartItemDTO();
            itemDTO.setId(itemEntity.getId());
            itemDTO.setProductId(itemEntity.getProduct().getId());
            itemDTO.setProductName(itemEntity.getProduct().getName());
            itemDTO.setProductImageUrl(itemEntity.getProduct().getImageUrl());
            itemDTO.setPriceAtAddition(itemEntity.getPrice());
            itemDTO.setQuantity(itemEntity.getQuantity());
            itemDTO.setSubTotal(subtotal);

            // thêm vào danh sách
            itemDTOs.add(itemDTO);

            // cộng dồn
            totalAmount = totalAmount.add(subtotal);
            totalItems += itemEntity.getQuantity();
        }

        CartDTO cartDTO = new CartDTO();
        cartDTO.setCartId(entity.getId());
        cartDTO.setUserId(entity.getUser().getId());
        cartDTO.setItems(itemDTOs);
        cartDTO.setTotalAmount(totalAmount);
        cartDTO.setTotalItems(totalItems);
        return cartDTO;
    }

    @Override
    public CartEntity layHoacTaoGioHang(Long userId) {
        CartEntity cart = cartRepository.findByUserId(userId).orElseGet(() -> {
            UserEntity user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                            "Không tìm thấy người dùng có Id là: " + userId));

            CartEntity newCart = new CartEntity();
            newCart.setUser(user);
            return cartRepository.save(newCart);
        });

        return cart;
    }

    @Override
    public void capNhatSoLuong(ProductEntity product, int newQuantity) {
        Long userId = securityUtils.getCurrentId();
        if (newQuantity < 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Số lượng không hợp lệ !!");

        CartEntity cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy giỏ hàng !!"));
        CartItemEntity itemCart = cartItemRepository.findByCart_IdAndProduct_Id(cart.getId(), product.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Sản phẩm không tồn tại trong giỏ hàng của bạn"));
        if (newQuantity == 0) {
            cartItemRepository.delete(itemCart);
        } else {
            if (product.getStock() < newQuantity) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Số lượng vượt quá tồn kho !!");
            } else {
                itemCart.setQuantity(newQuantity);
                cartItemRepository.save(itemCart);
            }
        }
    }

    @Override
    public void themSanPhamVaoGio(ProductEntity product, int quantity) {
        Long userId = securityUtils.getCurrentId();
        CartEntity cart = layHoacTaoGioHang(userId);
        Optional<CartItemEntity> itemOp = cartItemRepository.findByCart_IdAndProduct_Id(cart.getId(), product.getId());
        if (itemOp.isPresent()) {
            CartItemEntity item = itemOp.get();
            int newQuantity = item.getQuantity() + quantity;
            if (product.getStock() < newQuantity) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Số lượng vượt mức tồn kho !!");
            }
            item.setQuantity(newQuantity);
            cartItemRepository.save(item);
        } else {
            if (product.getStock() < quantity)
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Số lượng vượt mức tồn kho !!");
            CartItemEntity newItem = new CartItemEntity();
            newItem.setCart(cart);
            newItem.setPrice(product.getPrice());
            newItem.setQuantity(quantity);
            newItem.setProduct(product);
            cartItemRepository.save(newItem);
        }
    }

    @Override
    public BigDecimal tinhTongTienGioHang(CartEntity cart) {
        BigDecimal tongTien = BigDecimal.ZERO;
        List<CartItemEntity> items = cart.getItems();
        for (int i = 0; i < items.size(); i++) {
            BigDecimal thanhTien = items.get(i).getPrice().multiply(BigDecimal.valueOf(items.get(i).getQuantity()));
            tongTien = tongTien.add(thanhTien);
        }
        return tongTien;
    }

    @Override
    public void xoaMotSanPhamTrongGio(Long productId) {
        Long userId = securityUtils.getCurrentId();
        CartEntity cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy giỏ hàng !!"));
        CartItemEntity item = cartItemRepository.findByCart_IdAndProduct_Id(cart.getId(), productId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy sản phẩm để xóa"));
        cartItemRepository.delete(item);
    }

    @Override
    public void xoaNhieuSanPhamTrongGio(List<Long> CartItemIds) {
        Long userId = securityUtils.getCurrentId();
        CartEntity cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy giỏ hàng"));
        for (int i = 0; i < CartItemIds.size(); i++) {
            Optional<CartItemEntity> item = cartItemRepository.findById(CartItemIds.get(i));
            if (item.isPresent()) {
                if (!item.get().getCart().getId().equals(cart.getId())) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Sản phẩm không thuộc giỏ hàng");
                }
                cartItemRepository.delete(item.get());
            }
        }
    }

    @Override
    public int demSoSanPhamTrongGio() {
        int dem = 0;
        Long userId = securityUtils.getCurrentId();
        CartEntity cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy giỏ hàng"));
        List<CartItemEntity> items = cartItemRepository.findByCartId(cart.getId());
        for (int i = 0; i < items.size(); i++) {
            int soLuong = items.get(i).getQuantity();
            dem += soLuong;
        }
        return dem;
    }

    @Override
    public CartDTO xemGioHang() {
        Long userId = securityUtils.getCurrentId();
        CartEntity cartEntity = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy giỏ hàng !!"));

        return toCartDTO(cartEntity);
    }

}
