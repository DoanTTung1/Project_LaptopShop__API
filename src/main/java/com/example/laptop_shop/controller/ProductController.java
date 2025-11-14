package com.example.laptop_shop.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.laptop_shop.model.ProductDTO;
import com.example.laptop_shop.model.SearchCriteriaDTO;
import com.example.laptop_shop.service.productservice.ProductServiceInterface;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductController {
    private final ProductServiceInterface productService;

    @GetMapping
    public Page<ProductDTO> searchProducts(SearchCriteriaDTO criteria,
            @PageableDefault(size = 12, sort = "id") Pageable pageable) {
        Page<ProductDTO> result = productService.SearchAndPaginate(criteria, pageable);
        return result;
    }

    @GetMapping({ "/{id}" })
    public ResponseEntity<ProductDTO> getProductId(@PathVariable Long id) {
        ProductDTO productId = productService.findById(id);
        if (productId == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().body(productId);
    }
}