package com.example.laptop_shop.service.orderservice;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.laptop_shop.model.BuyNowRequestDTO;
import com.example.laptop_shop.model.OrderListDTO;
import com.example.laptop_shop.model.OrderRequestDTO;
import com.example.laptop_shop.model.OrderResponseDTO;
import com.example.laptop_shop.model.OrderUpdateStatusDTO;

public interface OrderService {
    //Tạo đơn hàng mới từ giỏ hàng của người dùng.
    OrderResponseDTO createOrderFromCart(OrderRequestDTO requestDTO);
    //Tạo đơn hàng mới từ chức năng "Mua Ngay" (chỉ 1 sản phẩm).
    OrderResponseDTO createOrderFromBuyNow(BuyNowRequestDTO requestDTO);
    //Lấy danh sách đơn hàng tóm tắt của người dùng đang đăng nhập.
    List<OrderListDTO> getMyOrders();
    //Lấy chi tiết đầy đủ của một đơn hàng theo ID.
    OrderResponseDTO getOrderDetails(Long orderId);
    //[ADMIN] Lấy tất cả đơn hàng (có phân trang).
    Page<OrderListDTO> getAllOrders(Pageable pageable);
    //Cập nhật trạng thái của một đơn hàng (Hủy, Xác nhận đã trả...).
    OrderResponseDTO updateOrderStatus(Long orderId, OrderUpdateStatusDTO statusDTO);
}
