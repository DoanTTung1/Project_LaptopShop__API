package com.example.laptop_shop.model;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class SearchCriteriaDTO {
    //Tìm kiếm theo tên hoặc mô tả
    private String nameOrDescription;
    // Lọc theo thương hiệu
    private String brand;
    //Lọc theo danh mục
    private Long categoryId;

    //Lọc theo khoảng giá
    private BigDecimal fromPrice;
    private BigDecimal toPrice;
    //Lọc theo tối thiểu tồn kho
    private Integer minStock;
}
