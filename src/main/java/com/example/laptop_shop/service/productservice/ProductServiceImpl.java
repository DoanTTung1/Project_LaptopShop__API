package com.example.laptop_shop.service.productservice;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.laptop_shop.model.ProductDTO;
import com.example.laptop_shop.model.SearchCriteriaDTO;
import com.example.laptop_shop.repository.ProductRepository;
import com.example.laptop_shop.repository.entity.ProductEntity;
import com.example.laptop_shop.specification.ProductSpecification;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductServiceInterface {
    private final ProductRepository productRepository;

    // Ham chuyen entity sang dto
    private ProductDTO toDTO(ProductEntity e) {
        ProductDTO dto = new ProductDTO();
        dto.setName(e.getName());
        dto.setBrand(e.getBrand());
        dto.setPrice(e.getPrice());
        dto.setId(e.getId());
        dto.setImage_url(e.getImageUrl());
        dto.setStock(e.getStock());
        if (e.getSupplier() != null) {
            dto.setSupplierId(e.getSupplier().getId());
            dto.setSupplierName(e.getSupplier().getName());
        }

        if (e.getCategory() != null) {
            dto.setCategoryId(e.getCategory().getId());
            dto.setCategoryName(e.getCategory().getName());
        }
        return dto;
    }

    @Override

    //Hàm tìm kiếm chính
    public Page<ProductDTO> SearchAndPaginate(SearchCriteriaDTO criteria, Pageable pageable) {
        List<ProductDTO> dtos = new ArrayList<>(); 
        Specification<ProductEntity> spec = ProductSpecification.filterProducts(criteria);
        Page<ProductEntity> entityPage = productRepository.findAll(spec,pageable);
        for(ProductEntity i:entityPage.getContent())
        {
             dtos.add(toDTO(i));
        }
        Page<ProductDTO> result = new PageImpl<ProductDTO>(dtos,entityPage.getPageable(),entityPage.getTotalElements());

        return result;
    }

    @Override
    public ProductDTO findById(Long id) {
        ProductEntity entity = productRepository.findById(id).orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"Không tìm thấy sản phẩm"));
        return toDTO(entity);
    }

}
