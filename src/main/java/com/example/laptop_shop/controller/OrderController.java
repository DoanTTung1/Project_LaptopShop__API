package com.example.laptop_shop.controller;

// Import các DTOs từ package model
import com.example.laptop_shop.model.BuyNowRequestDTO;
import com.example.laptop_shop.model.OrderListDTO;
import com.example.laptop_shop.model.OrderRequestDTO;
import com.example.laptop_shop.model.OrderResponseDTO;
import com.example.laptop_shop.model.OrderUpdateStatusDTO;

// Import Service (giả định package)
import com.example.laptop_shop.service.orderservice.OrderService;

// Import các thư viện cần thiết
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; // Cho phân quyền
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    /**
     * POST /api/orders
     * Tạo một đơn hàng mới từ giỏ hàng.
     * Cần quyền: USER
     */
    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<OrderResponseDTO> createOrderFromCart(
            @Validated @RequestBody OrderRequestDTO requestDTO) {
        // @Valid sẽ kích hoạt validation nếu bạn có đặt trên DTO
        OrderResponseDTO newOrder = orderService.createOrderFromCart(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(newOrder);
    }

    /**
     * POST /api/orders/buy-now
     * Tạo đơn hàng "Mua Ngay" (không qua giỏ hàng).
     * Cần quyền: USER
     */
    @PostMapping("/buy-now")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<OrderResponseDTO> createOrderBuyNow(
            @Validated @RequestBody BuyNowRequestDTO requestDTO) {
        OrderResponseDTO newOrder = orderService.createOrderFromBuyNow(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(newOrder);
    }

    /**
     * GET /api/orders/my-orders
     * Lấy danh sách đơn hàng của user đang đăng nhập.
     * Cần quyền: USER
     */
    @GetMapping("/my-orders")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<OrderListDTO>> getMyOrders() {
        List<OrderListDTO> orders = orderService.getMyOrders();
        return ResponseEntity.ok(orders);
    }

    /**
     * GET /api/orders
     * [ADMIN] Lấy tất cả đơn hàng, có phân trang.
     * Cần quyền: ADMIN
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<OrderListDTO>> getAllOrders(
            // @PageableDefault cho phép thiết lập mặc định cho phân trang
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<OrderListDTO> orders = orderService.getAllOrders(pageable);
        return ResponseEntity.ok(orders);
    }

    /**
     * GET /api/orders/{id}
     * Lấy chi tiết một đơn hàng theo ID.
     * Cần quyền: ADMIN hoặc USER (là chủ đơn hàng)
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<OrderResponseDTO> getOrderById(@PathVariable Long id) {
        // Logic kiểm tra chủ đơn hàng đã được thực hiện trong Service
        OrderResponseDTO orderDetails = orderService.getOrderDetails(id);
        return ResponseEntity.ok(orderDetails);
    }

    /**
     * PATCH /api/orders/{id}/status
     * Cập nhật trạng thái đơn hàng.
     * Cần quyền: ADMIN (mọi trạng thái) hoặc USER (chỉ được Hủy đơn PENDING)
     */
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<OrderResponseDTO> updateOrderStatus(
            @PathVariable Long id,
            @Validated @RequestBody OrderUpdateStatusDTO statusDTO) {
        // Logic phân quyền chi tiết được xử lý trong Service
        OrderResponseDTO updatedOrder = orderService.updateOrderStatus(id, statusDTO);
        return ResponseEntity.ok(updatedOrder);
    }
}