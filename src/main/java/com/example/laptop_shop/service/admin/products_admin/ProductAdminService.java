package com.example.laptop_shop.service.admin.products_admin;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.laptop_shop.model.ProductDTO;

public interface ProductAdminService {

    // thêm mới và cập nhật toàn bộ
    ProductDTO save(ProductDTO productDTO);

    ProductDTO patchUpdate(Long id, Map<String, Object> updates);

    void softDeleteById(Long id);

    void softDeleteByIds(List<Long> ids);

    ProductDTO findById(Long id);

    Page<ProductDTO> getAllActiveProducts(Pageable pageable);

    Page<ProductDTO> getAllProductsDeleted(Pageable pageable);

    void restoreProduct(Long id);
    void hardDeleteById(Long id);
    void hardDeleteByIds(List<Long> ids);
}
