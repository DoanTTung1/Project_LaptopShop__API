package com.example.laptop_shop.controller.admin;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.example.laptop_shop.model.ProductDTO;
import com.example.laptop_shop.service.admin.products_admin.ImageService;
import com.example.laptop_shop.service.admin.products_admin.ProductAdminService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/admin/products")
@PreAuthorize("hasRole('ADMIN')")
public class AdminProductController {
    private final ProductAdminService productAdminService;
    private final ImageService imageService;

    // Hàm lấy toàn bộ sản phẩm active = true
    @GetMapping
    public ResponseEntity<Page<ProductDTO>> getAllProduct(@PageableDefault(size = 12, sort = "id") Pageable pageable) {
        return ResponseEntity.ok().body(productAdminService.getAllActiveProducts(pageable));
    }

    // Hàm tạo mới
    @PostMapping()
    public ResponseEntity<ProductDTO> createProducts(@RequestBody ProductDTO productDTO) {
        if (productDTO.getId() != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Id phải là null khi tạo mới sản phẩm");
        }
        ProductDTO newProductDTO = productAdminService.save(productDTO);
        ResponseEntity<ProductDTO> result = new ResponseEntity<>(newProductDTO, HttpStatus.CREATED);
        return result;
    }

    // Hàm cập nhật:
    // Kiểu PUT cập nhật toàn bộ
    @PutMapping("/{id}")
    public ResponseEntity<ProductDTO> putUpdatedProduct(@PathVariable Long id, @RequestBody ProductDTO productDTO) {
        if (id == null || !id.equals(productDTO.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Id trong đường dẫn và Id trong dữ liệu không khớp hoặc bị thiếu");
        }
        ProductDTO newPutUpdated = productAdminService.save(productDTO);
        ResponseEntity<ProductDTO> result = new ResponseEntity<>(newPutUpdated, HttpStatus.OK);
        return result;
    }

    // Cập nhật kiểu Patch
    @PatchMapping("/{id}")
    public ResponseEntity<ProductDTO> patchUpdatedProduct(@PathVariable Long id,
            @RequestBody Map<String, Object> updates) {
        ProductDTO newPatchUpdated = productAdminService.patchUpdate(id, updates);
        ResponseEntity<ProductDTO> result = new ResponseEntity<>(newPatchUpdated, HttpStatus.OK);
        return result;
    }

    @PostMapping(value = "/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadImage(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("File không được để trống!");
        }

        ProductDTO dto = new ProductDTO();
        dto.setId(id);
        dto.setImageFile(file);
        try {
            imageService.updatedImage(dto);
            return ResponseEntity.ok("Upload ảnh thành công cho sản phẩm ID: " + id);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi upload: " + e.getMessage());
        }
    }

    @DeleteMapping("/image")
    public ResponseEntity<String> deleteImage(@RequestParam("fileName") String fileName) {
        try {
            imageService.deleteImage(fileName);
            return ResponseEntity.ok("Đã xóa ảnh " + fileName);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi xóa ảnh: " + e.getMessage());

        }
    }

    @DeleteMapping("/hard")
    public ResponseEntity<?> hardDeteleProducts(@RequestBody List<Long> ids) {
        if (ids == null || ids.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Danh sách id không được để trống");
        productAdminService.hardDeleteByIds(ids);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/hard/{id}")
    public ResponseEntity<?> hardDeleteProductId(@PathVariable Long id) {
        productAdminService.hardDeleteById(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/soft/{id}")
    public ResponseEntity<?> softDeleteProducts(@PathVariable Long id) {
        productAdminService.softDeleteById(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/soft")
    public ResponseEntity<?> softDeleteProducts(@RequestBody List<Long> id) {
        productAdminService.softDeleteByIds(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProductId(@PathVariable Long id) {
        return ResponseEntity.ok().body(productAdminService.findById(id));
    }

    @GetMapping("/deleted")
    public ResponseEntity<?> getDeletedProducts(@PageableDefault(size = 12, sort = "id") Pageable pageable) {
        return ResponseEntity.ok().body(productAdminService.getAllProductsDeleted(pageable));
    }

    @PostMapping("/restore/{id}")
    public ResponseEntity<?> restoreProduct(@PathVariable long id) {
        productAdminService.restoreProduct(id);
        return ResponseEntity.noContent().build();
    }

}
