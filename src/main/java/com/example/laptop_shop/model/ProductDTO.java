package com.example.laptop_shop.model;

import java.math.BigDecimal;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor 
@AllArgsConstructor 
public class ProductDTO {
    private Long id;
    private String name;
    private String description;
    private String brand;
    private BigDecimal price;
    private Integer stock;
    private String image_url;
    private Long categoryId;
    private String categoryName;
    private Long supplierId;
    private String supplierName;
    private Boolean active;
    private MultipartFile imageFile;
}
