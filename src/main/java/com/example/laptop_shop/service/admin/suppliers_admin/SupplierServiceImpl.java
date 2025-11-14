package com.example.laptop_shop.service.admin.suppliers_admin;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.laptop_shop.model.SupplierAdminDTO;
import com.example.laptop_shop.repository.SupplierRepository;
import com.example.laptop_shop.repository.entity.SupplierEntity;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SupplierServiceImpl implements SupplierService {

    private final SupplierRepository supplierRepository;

    private SupplierAdminDTO toDTO(SupplierEntity entity) {
        SupplierAdminDTO dto = new SupplierAdminDTO();
        dto.setId(entity.getId());
        dto.setAddress(entity.getAddress());
        dto.setEmail(entity.getEmail());
        dto.setName(entity.getName());
        dto.setPhone(entity.getPhone());
        return dto;
    }

    @Override
    public SupplierAdminDTO createSupplier(SupplierAdminDTO supDTO) {
        if (supplierRepository.existsByName(supDTO.getName()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tên nhà cung cấp đã tồn tại !!");
        if (supplierRepository.existsByEmail(supDTO.getEmail()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email đã tồn tại !!");
        if (supplierRepository.existsByPhone(supDTO.getPhone()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Số điện thoại đã tồn tại !!");
        SupplierEntity newSup = new SupplierEntity();
        newSup.setAddress(supDTO.getAddress());
        newSup.setEmail(supDTO.getEmail());
        newSup.setName(supDTO.getName());
        newSup.setPhone(supDTO.getPhone());
        return toDTO(supplierRepository.save(newSup));
    }

    @Override
    public void deleteSupplier(Long supplierId) {
        SupplierEntity entity = supplierRepository.findById(supplierId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Không tìm thấy nhà cung cấp có id là: " + supplierId + " để xóa !!"));
        supplierRepository.delete(entity);

    }

    @Override
    public List<SupplierAdminDTO> getAllSuppliers() {
        List<SupplierAdminDTO> dtos = new ArrayList<>();
        List<SupplierEntity> entities = supplierRepository.findAll();
        for (int i = 0; i < entities.size(); i++) {
            SupplierEntity entity = entities.get(i);
            SupplierAdminDTO dto = toDTO(entity);
            dtos.add(dto);
        }
        return dtos;
    }

    @Override
    public SupplierAdminDTO getSupplierId(Long supplierId) {
        SupplierEntity entity = supplierRepository.findById(supplierId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Không tìm thấy nhà cung cấp với id là: " + supplierId));

        return toDTO(entity);
    }

    @Override
    public SupplierAdminDTO updatedSupplier(SupplierAdminDTO dto) {
        SupplierEntity supEntity = supplierRepository.findById(dto.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Không tìm thấy nhà cung cấp có Id là: " + dto.getId()));
        if (dto.getName() != null && !dto.getName().trim().isEmpty()) {
            if (supplierRepository.existsByName(dto.getName())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tên nhà cung cấp đã tồn tại !!");
            } else {
                supEntity.setName(dto.getName());
            }
        }
        if (dto.getEmail() != null && !dto.getEmail().trim().isEmpty()) {

            if (supplierRepository.existsByEmail(dto.getEmail())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email đã tồn tại !!");
            } else {
                supEntity.setEmail(dto.getEmail());
            }
        }
        if (dto.getPhone() != null && !dto.getPhone().trim().isEmpty()) {

            if (supplierRepository.existsByPhone(dto.getPhone())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Số điện thoại đã tồn tại !!");
            } else {
                supEntity.setPhone(dto.getPhone());
            }
        }
        if (dto.getAddress() != null && !dto.getAddress().trim().isEmpty()) {
            supEntity.setAddress(dto.getAddress());
        }
        return toDTO(supEntity);
    }

}
