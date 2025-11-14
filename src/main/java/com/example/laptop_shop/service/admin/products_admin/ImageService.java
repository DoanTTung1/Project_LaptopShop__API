package com.example.laptop_shop.service.admin.products_admin;



import com.example.laptop_shop.model.ProductDTO;

public interface ImageService {
    void updatedImage(ProductDTO dto);
    void deleteImage(String fileName);
}
