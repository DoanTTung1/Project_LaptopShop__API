package com.example.laptop_shop.service.orderservice;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.laptop_shop.model.BuyNowRequestDTO;
import com.example.laptop_shop.model.CustomerDTO;
import com.example.laptop_shop.model.OrderItemResponseDTO;
import com.example.laptop_shop.model.OrderListDTO;
import com.example.laptop_shop.model.OrderRequestDTO;
import com.example.laptop_shop.model.OrderResponseDTO;
import com.example.laptop_shop.model.OrderUpdateStatusDTO;
import com.example.laptop_shop.repository.CartItemRepository;
import com.example.laptop_shop.repository.CartRepository;
import com.example.laptop_shop.repository.OrderItemRepository;
import com.example.laptop_shop.repository.OrderRepository;
import com.example.laptop_shop.repository.ProductRepository;
import com.example.laptop_shop.repository.UserRepository;
import com.example.laptop_shop.repository.entity.CartEntity;
import com.example.laptop_shop.repository.entity.CartItemEntity;
import com.example.laptop_shop.repository.entity.OrderEntity;
import com.example.laptop_shop.repository.entity.OrderItemEntity;
import com.example.laptop_shop.repository.entity.OrderStatus;
import com.example.laptop_shop.repository.entity.ProductEntity;
import com.example.laptop_shop.repository.entity.Role;
import com.example.laptop_shop.repository.entity.UserEntity;
import com.example.laptop_shop.security.SecurityUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final SecurityUtils securityUtils;

    private OrderResponseDTO mapToOrderResponseDTO(OrderEntity order) {
        OrderResponseDTO dto = new OrderResponseDTO();
        dto.setId(order.getId());
        dto.setStatus(order.getStatus());
        dto.setTotalPrice(order.getTotalPrice());
        dto.setCreatedAt(order.getCreatedAt());
        dto.setPhone(order.getPhone());

        // (MỚI) 1. THÊM ĐỊA CHỈ GIAO HÀNG
        dto.setShippingAddress(order.getShippingAddress()); // <-- (THÊM DÒNG NÀY)

        // Tạo Customer DTO
        CustomerDTO customer = new CustomerDTO();
        customer.setUserId(order.getUser().getId());
        customer.setUsername(order.getUser().getUsername());

        // (MỚI) 2. THÊM EMAIL (Rất quan trọng cho Frontend)
        customer.setEmail(order.getUser().getEmail()); // <-- (THÊM DÒNG NÀY)

        dto.setCustomer(customer);

        // Tạo danh sách Item DTO
        List<OrderItemResponseDTO> itemDTOs = order.getOrderItems().stream()
                .map(item -> {
                    OrderItemResponseDTO itemDTO = new OrderItemResponseDTO();
                    itemDTO.setId(item.getId()); // (MỚI) Thêm ID của OrderItem
                    itemDTO.setProductId(item.getProduct().getId());
                    itemDTO.setProductName(item.getProduct().getName());
                    itemDTO.setQuantity(item.getQuantity());
                    itemDTO.setPriceAtPurchase(item.getPrice()); // Đổi tên thành priceAtOrder
                    itemDTO.setProductImageUrl(item.getProduct().getImageUrl());
                    return itemDTO;
                }).collect(Collectors.toList());

        dto.setItems(itemDTOs);
        return dto;
    }

    private OrderListDTO mapToOrderListDTO(OrderEntity order) {
        OrderListDTO dto = new OrderListDTO();
        dto.setId(order.getId());
        dto.setStatus(order.getStatus());
        dto.setTotalPrice(order.getTotalPrice());
        dto.setCreatedAt(order.getCreatedAt());

        // Đếm số lượng loại sản phẩm
        int totalItems = (order.getOrderItems() != null) ? order.getOrderItems().size() : 0;
        dto.setTotalItems(totalItems);

        return dto;
    }

    private UserEntity getCurrentUser() {
        Long userId = securityUtils.getCurrentId();
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Không tìm thấy người dùng có Id là: " + userId));
    }

    @Override
    public OrderResponseDTO createOrderFromBuyNow(BuyNowRequestDTO requestDTO) {
        UserEntity currentUser = getCurrentUser();

        // 1. Lấy sản phẩm
        ProductEntity product = productRepository.findById(requestDTO.getProductId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy sản phẩm."));

        // 2. Kiểm tra tồn kho
        if (product.getStock() < requestDTO.getQuantity()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Sản phẩm " + product.getName() + " không đủ hàng.");
        }

        // 3. Tạo Đơn hàng mới
        OrderEntity newOrder = new OrderEntity();
        newOrder.setUser(currentUser);
        newOrder.setStatus(OrderStatus.PENDING);
        newOrder.setShippingAddress(requestDTO.getShippingAddress());
        newOrder.setPhone(requestDTO.getPhone());

        // 4. Giảm tồn kho
        product.setStock(product.getStock() - requestDTO.getQuantity());
        productRepository.save(product);

        // 5. Tạo OrderItem
        OrderItemEntity orderItem = new OrderItemEntity();
        orderItem.setOrder(newOrder);
        orderItem.setProduct(product);
        orderItem.setQuantity(requestDTO.getQuantity());
        orderItem.setPrice(product.getPrice()); // Lấy giá hiện tại của sản phẩm

        // 6. Tính tổng tiền
        BigDecimal totalPrice = product.getPrice().multiply(BigDecimal.valueOf(requestDTO.getQuantity()));
        newOrder.setTotalPrice(totalPrice);

        // 7. Gán quan hệ (quan trọng)
        List<OrderItemEntity> items = new ArrayList<>();
        items.add(orderItem);
        newOrder.setOrderItems(items);

        // 8. Lưu Order (và OrderItem sẽ được lưu theo)
        orderItem.setOrder(newOrder); // Đảm bảo gán 2 chiều
        OrderEntity savedOrder = orderRepository.save(newOrder);
        // (Lưu ý: Bạn có thể cần lưu orderItem riêng nếu CascadeType không đúng)
        // orderItemRepository.save(orderItem); // Có thể cần dòng này

        return mapToOrderResponseDTO(savedOrder);
    }

    @Override
    public OrderResponseDTO createOrderFromCart(OrderRequestDTO requestDTO) {
        UserEntity currentUser = getCurrentUser();
        CartEntity cart = cartRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy giỏ hàng."));
        List<CartItemEntity> cartItems = cartItemRepository.findByCartId(cart.getId());
        if (cartItems.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Giỏ hàng của bạn đang trống.");
        }
        OrderEntity newOrder = new OrderEntity();
        newOrder.setUser(currentUser);
        newOrder.setStatus(OrderStatus.PENDING);
        newOrder.setShippingAddress(requestDTO.getShippingAddress());
        newOrder.setPhone(requestDTO.getPhone());
        newOrder = orderRepository.save(newOrder);
        BigDecimal totalPrice = BigDecimal.ZERO;
        List<OrderItemEntity> orderItemsList = new ArrayList<>();
        // 3. Khóa, Kiểm tra tồn kho, và Chuyển CartItem -> OrderItem
        for (CartItemEntity item : cartItems) {
            // Khóa sản phẩm để xử lý đồng thời (concurrency)
            ProductEntity product = productRepository.findById(item.getProduct().getId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                            "Sản phẩm ID " + item.getProduct().getId() + " không còn tồn tại."));

            if (!product.isActive()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Sản phẩm " + product.getName() + " đã ngừng kinh doanh.");
            }

            // Kiểm tra tồn kho
            if (product.getStock() < item.getQuantity()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Sản phẩm " + product.getName() + " không đủ hàng (chỉ còn " + product.getStock() + ").");
            }

            // 4. Giảm tồn kho
            product.setStock(product.getStock() - item.getQuantity());
            productRepository.save(product);

            // 5. Tạo OrderItem
            OrderItemEntity orderItem = new OrderItemEntity();
            orderItem.setOrder(newOrder);
            orderItem.setProduct(product);
            orderItem.setQuantity(item.getQuantity());
            orderItem.setPrice(item.getPrice()); // Lấy giá từ giỏ hàng
            orderItemsList.add(orderItem);

            // 6. Tính tổng tiền
            totalPrice = totalPrice.add(item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
        }

        // 7. Lưu tất cả OrderItem
        orderItemRepository.saveAll(orderItemsList);

        // 8. Cập nhật tổng tiền và danh sách item cho Order (lưu lần 2)
        newOrder.setTotalPrice(totalPrice);
        newOrder.setOrderItems(orderItemsList);
        OrderEntity savedOrder = orderRepository.save(newOrder);

        // 9. Xóa giỏ hàng
        cartItemRepository.deleteAll(cartItems);
        cart.setTotalPrice(BigDecimal.ZERO);
        cartRepository.save(cart);
        // 10. Chuyển đổi sang DTO để trả về
        return mapToOrderResponseDTO(savedOrder);
    }

    @Override
    public Page<OrderListDTO> getAllOrders(Pageable pageable) {
        Page<OrderEntity> orderPage = orderRepository.findAll(pageable);
        return orderPage.map(this::mapToOrderListDTO);
    }

    @Override
    public List<OrderListDTO> getMyOrders() {
        UserEntity currentUser = getCurrentUser();
        List<OrderEntity> orders = orderRepository.findByUserId(currentUser.getId());

        return orders.stream()
                .map(this::mapToOrderListDTO)
                .collect(Collectors.toList());
    }

    @Override
    public OrderResponseDTO getOrderDetails(Long orderId) {
        UserEntity currentUser = getCurrentUser();
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Không tìm thấy đơn hàng ID " + orderId));

        if (currentUser.getRole() != Role.ADMIN && !order.getUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("Bạn không có quyền xem đơn hàng này.");
        }

        return mapToOrderResponseDTO(order);
    }

    @Override
    public OrderResponseDTO updateOrderStatus(Long orderId, OrderUpdateStatusDTO statusDTO) {
        UserEntity currentUser = getCurrentUser();
        OrderEntity order = orderRepository.findById(orderId) // Lấy kèm items
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Không tìm thấy đơn hàng ID " + orderId));

        OrderStatus newStatus = statusDTO.getStatus();
        OrderStatus oldStatus = order.getStatus();

        // Nếu trạng thái không đổi -> trả về luôn
        if (oldStatus == newStatus) {
            return mapToOrderResponseDTO(order);
        }

        // Logic phân quyền cập nhật
        if (currentUser.getRole() == Role.USER) {
            // User chỉ được phép HỦY đơn hàng của chính mình
            if (!order.getUser().getId().equals(currentUser.getId())) {
                throw new AccessDeniedException("Bạn không có quyền cập nhật đơn hàng này.");
            }
            // User chỉ được cập nhật sang CANCELLED
            if (newStatus != OrderStatus.CANCELLED) {
                throw new AccessDeniedException("Bạn chỉ có quyền hủy đơn hàng.");
            }
            // Và chỉ khi đơn hàng đang PENDING
            if (oldStatus != OrderStatus.PENDING) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Không thể hủy đơn hàng đã được xử lý hoặc đã hủy.");
            }
        }
        // (Nếu là ADMIN thì có toàn quyền)

        // Logic nghiệp vụ: Hoàn kho (restock) nếu hủy đơn
        if (newStatus == OrderStatus.CANCELLED && oldStatus != OrderStatus.CANCELLED) {
            // Lặp qua các item trong đơn hàng
            for (OrderItemEntity item : order.getOrderItems()) {
                ProductEntity product = productRepository.findById(item.getProduct().getId()).orElse(null);
                if (product != null) {
                    // Cộng trả lại số lượng
                    product.setStock(product.getStock() + item.getQuantity());
                    productRepository.save(product);
                }
            }
        }

        // Cập nhật trạng thái mới và lưu
        order.setStatus(newStatus);
        OrderEntity updatedOrder = orderRepository.save(order);
        return mapToOrderResponseDTO(updatedOrder);
    }
}
