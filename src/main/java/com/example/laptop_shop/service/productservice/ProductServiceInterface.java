package com.example.laptop_shop.service.productservice;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.laptop_shop.model.ProductDTO;
import com.example.laptop_shop.model.SearchCriteriaDTO;

public interface ProductServiceInterface {

    // Tìm kiếm sản phẩm theo id
    ProductDTO findById(Long id);

    // Tìm kiếm nhiều sản phẩm
    Page<ProductDTO> SearchAndPaginate(SearchCriteriaDTO criteria, Pageable pageable);

    
}
