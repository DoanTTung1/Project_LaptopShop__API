package com.example.laptop_shop.service.admin.products_admin;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.laptop_shop.model.ProductDTO;
import com.example.laptop_shop.repository.ProductRepository;
import com.example.laptop_shop.repository.entity.ProductEntity;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {
    private final ProductRepository productRepository;
    private final StorageService storageService;

    @Override
    public void deleteImage(String fileName) {
        if (fileName != null && !fileName.isEmpty()) {
            storageService.delete(fileName);
            Optional<ProductEntity> entity = productRepository.findByImageUrl(fileName);
            if (entity.isPresent()) {
                ProductEntity e = entity.get();
                e.setImageUrl(null);
                productRepository.save(e);
            }
        } else {
            throw new RuntimeException("Ảnh không tồn tại để xóa");
        }
    }

    @Override
    public void updatedImage(ProductDTO dto) {
        MultipartFile file = dto.getImageFile();
        if (file != null && !file.isEmpty()) {
            Optional<ProductEntity> entity = productRepository.findById(dto.getId());
            if (entity.isPresent()) {
                ProductEntity e = entity.get();
                if (e.getImageUrl() != null)
                    storageService.delete(e.getImageUrl());
                String fileName = storageService.store(dto.getImageFile());
                e.setImageUrl(fileName);
                productRepository.save(e);
            } else
                throw new RuntimeException("Không tìm thấy sản phẩm có id là: " + dto.getId());
        }

    }
}
