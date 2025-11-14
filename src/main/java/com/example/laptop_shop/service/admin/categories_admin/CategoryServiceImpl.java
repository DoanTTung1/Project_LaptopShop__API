package com.example.laptop_shop.service.admin.categories_admin;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.laptop_shop.model.CategoryDTO;
import com.example.laptop_shop.repository.CategoryRepository;
import com.example.laptop_shop.repository.entity.CategoryEntity;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    private CategoryDTO toDTO(CategoryEntity e) {
        CategoryDTO dto = new CategoryDTO();
        dto.setName(e.getName());
        dto.setDescription(e.getDescription());
        dto.setId(e.getId());
        return dto;
    }

    private CategoryEntity toEntity(CategoryDTO dto) {
        CategoryEntity entity = new CategoryEntity();
        if (dto.getId() != null) {
            entity = categoryRepository.findById(dto.getId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                            "Không tìm thấy danh mục có id là: " + dto.getId()));

        }
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        return entity;
    }

    @Override
    public void deleteCategory(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Không tìm thấy danh mục có id là: " + id + " để xóa!");
        }
        categoryRepository.deleteById(id);
    }

    @Override
    public List<CategoryDTO> findAll() {
        List<CategoryDTO> dtos = new ArrayList<>();
        List<CategoryEntity> entities = categoryRepository.findAll();
        for (int i = 0; i < entities.size(); i++) {
            CategoryEntity entity = entities.get(i);
            dtos.add(toDTO(entity));
        }
        return dtos;
    }

    @Override
    public CategoryEntity findEntityByName(String categoryName) {
        if (categoryName == null || categoryName.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tên danh mục không được để trống!");
        }
        return categoryRepository.findByNameIgnoreCase(categoryName).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy danh mục " + categoryName));
    }

    @Override
    public CategoryDTO findById(Long id) {
        CategoryEntity entity = categoryRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy danh mục có Id là: " + id));

        return toDTO(entity);
    }

    @Transactional
    @Override
    public CategoryDTO newCreate(CategoryDTO categoryDTO) {
        if (categoryDTO.getId() != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Khi tạo mới id phải là null");
        }
        if (categoryDTO.getName() == null || categoryDTO.getName().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Khi tạo mới tên danh mục không được để trống");
        }
        Optional<CategoryEntity> checkTenCategoryTrung = categoryRepository.findByNameIgnoreCase(categoryDTO.getName());
        if (checkTenCategoryTrung.isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Khi tạo mới tên danh mục không được phép trùng lặp");
        }
        CategoryEntity entity = categoryRepository.save(toEntity(categoryDTO));
        CategoryDTO result = toDTO(entity);
        return result;
    }

    @Transactional
    @Override
    public CategoryDTO updated(Long id, CategoryDTO categoryDTO) {
        CategoryEntity entity = categoryRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Danh mục không tồn tại để cập nhật"));

        if (categoryDTO.getName() != null) {
            if (categoryDTO.getName().trim().isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tên danh mục không được để trống");
            }
            Optional<CategoryEntity> e = categoryRepository.findByNameIgnoreCase(categoryDTO.getName());
            if (e.isPresent()) {
                if (!id.equals(e.get().getId()))
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tên danh mục đã thuộc danh mục khác");
                entity.setName(categoryDTO.getName());

            }
        }
        if (categoryDTO.getDescription() != null && !categoryDTO.getDescription().trim().isEmpty()) {
            entity.setDescription(categoryDTO.getDescription());
        }
        CategoryEntity result = categoryRepository.save(entity);
        return toDTO(result);

    }

}
