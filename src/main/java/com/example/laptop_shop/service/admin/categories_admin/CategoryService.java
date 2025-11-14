package com.example.laptop_shop.service.admin.categories_admin;

import java.util.List;

import com.example.laptop_shop.model.CategoryDTO;
import com.example.laptop_shop.repository.entity.CategoryEntity;

public interface CategoryService {

    CategoryDTO newCreate(CategoryDTO categoryDTO);

    CategoryDTO updated(Long id,CategoryDTO categoryDTO);

    List<CategoryDTO> findAll();

    void deleteCategory(Long id);

    CategoryEntity findEntityByName(String categoryName);

    CategoryDTO findById(Long id);
}
