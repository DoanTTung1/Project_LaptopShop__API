package com.example.laptop_shop.service.admin.suppliers_admin;

import java.util.List;

import com.example.laptop_shop.model.SupplierAdminDTO;

public interface SupplierService {

    List<SupplierAdminDTO>  getAllSuppliers();
    SupplierAdminDTO getSupplierId(Long supplierId);
    SupplierAdminDTO createSupplier(SupplierAdminDTO supDTO);
    SupplierAdminDTO updatedSupplier(SupplierAdminDTO dto);
    void deleteSupplier(Long supplierId);
}
