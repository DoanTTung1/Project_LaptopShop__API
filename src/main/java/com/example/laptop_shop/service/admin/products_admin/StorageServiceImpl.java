package com.example.laptop_shop.service.admin.products_admin;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class StorageServiceImpl implements StorageService {

    private final Path root = Paths.get("uploads");

    public StorageServiceImpl() {
        try {
            if (!Files.exists(root)) {
                Files.createDirectories(root);
                log.info("Tạo thư mục uploads thành công");
            }
        } catch (IOException e) {
            log.error("Lỗi khi tạo thư mục uploads", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Không thể tạo file uploads!");
        }
    }

    @Override
    public void delete(String fileName) {

        if (fileName != null && !fileName.isEmpty()) {
            try {
                Files.deleteIfExists(root.resolve(fileName));
                log.info("Xóa ảnh thành công");
            } catch (IOException e) {
                log.error("Không thể xóa ảnh", e);
                throw new RuntimeException("Xóa ảnh thất bại");
            }

        }
    }

    @Override
    public String store(MultipartFile file) {
        if (file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File rỗng!");
        }
        String OriginalName = file.getOriginalFilename();
        if (OriginalName == null || !OriginalName.contains("."))
            throw new RuntimeException("Tên file không hợp lệ!");
        Integer pos = OriginalName.lastIndexOf(".");
        String ext = OriginalName.substring(pos + 1).toLowerCase();
        List<String> ListExt = List.of("jpg", "jpeg", "png","webp");
        if (!ListExt.contains(ext)) {
            throw new RuntimeException("Chỉ cho phép các định dạng ảnh jpg,jpeg,png");
        }
        String newNameFile = UUID.randomUUID().toString() + "." + ext;
        try (InputStream fileStream = file.getInputStream();) {
            Path duongDan = root.resolve(newNameFile);

            Files.copy(fileStream, duongDan, StandardCopyOption.REPLACE_EXISTING);
            log.info("uploads file thành công:{}", newNameFile);
        } catch (IOException e) {
            log.error("Không thể lưu file", e);
            throw new RuntimeException("Upload ảnh thất bại");
        }

        return newNameFile;
    }

}
