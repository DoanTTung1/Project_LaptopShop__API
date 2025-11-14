package com.example.laptop_shop.controller.admin;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.laptop_shop.model.SupplierAdminDTO;
import com.example.laptop_shop.service.admin.suppliers_admin.SupplierService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/suppliers")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminSupplierController {
    private final SupplierService supplierService;

    @GetMapping
    public ResponseEntity<List<SupplierAdminDTO>> getAllSuppliers() {
        return ResponseEntity.ok().body(supplierService.getAllSuppliers());
    }

    @PostMapping
    public ResponseEntity<SupplierAdminDTO> createSupplier(@RequestBody SupplierAdminDTO dto) {
        if (dto.getName() == null || dto.getName().trim().isEmpty() || dto.getAddress() == null
                || dto.getAddress().trim().isEmpty() || dto.getPhone() == null || dto.getPhone().trim().isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Vui lòng nhập đầy đủ thông tin nhà cung cấp để tạo mới ");
        return ResponseEntity.ok().body(supplierService.createSupplier(dto));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<SupplierAdminDTO> updatedSupplier(@PathVariable Long id, SupplierAdminDTO dto) {
        dto.setId(id);
        return ResponseEntity.ok().body(supplierService.updatedSupplier(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SupplierAdminDTO> getSupplierId(@PathVariable Long id) {
        return ResponseEntity.ok().body(supplierService.getSupplierId(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSupplier(@PathVariable Long id) {
        supplierService.deleteSupplier(id);
        return ResponseEntity.noContent().build();
    }
}
