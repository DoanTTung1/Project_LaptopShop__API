package com.example.laptop_shop.service.admin.products_admin;

import org.springframework.web.multipart.MultipartFile;

public interface StorageService {

    String store(MultipartFile file);
    void delete(String fileName);
}
