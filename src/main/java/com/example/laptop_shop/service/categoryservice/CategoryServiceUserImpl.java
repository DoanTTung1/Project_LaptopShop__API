package com.example.laptop_shop.service.categoryservice;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.laptop_shop.model.CategoryDTO;
import com.example.laptop_shop.repository.CategoryRepository;
import com.example.laptop_shop.repository.entity.CategoryEntity;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryServiceUserImpl implements CategoryServiceUserInterface {

    private final CategoryRepository categoryRepository;

    // Chuyển entity sang DTO
    private CategoryDTO toDTO(CategoryEntity entity) {
        CategoryDTO dto = new CategoryDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        return dto;
    }

    @Override
    public List<CategoryDTO> getAllCategory() {
        List<CategoryDTO> cateDTOS = new ArrayList<>();
        List<CategoryEntity> cateEntities = categoryRepository.findAll();

        for (CategoryEntity entity : cateEntities) {
            cateDTOS.add(toDTO(entity));
        }

        return cateDTOS;
    }
}
