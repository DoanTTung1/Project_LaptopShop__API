package com.example.laptop_shop.service.admin.products_admin;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.example.laptop_shop.model.ProductDTO;
import com.example.laptop_shop.repository.CategoryRepository;
import com.example.laptop_shop.repository.ProductRepository;
import com.example.laptop_shop.repository.SupplierRepository;
import com.example.laptop_shop.repository.entity.CategoryEntity;
import com.example.laptop_shop.repository.entity.ProductEntity;
import com.example.laptop_shop.repository.entity.SupplierEntity;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductAdminServiceImpl implements ProductAdminService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final SupplierRepository supplierRepository;
    private final StorageService storageService;
    private final ImageService imageService;

    // Hàm chuyển đổi dto sang entity:
    private ProductEntity toEntity(ProductDTO dto) {
        ProductEntity e = (dto.getId() != null) ? productRepository.findById(dto.getId()).orElse(new ProductEntity())
                : new ProductEntity();
        if (dto.getName() != null)
            e.setName(dto.getName());
        if (dto.getBrand() != null)
            e.setBrand(dto.getBrand());
        if (dto.getPrice() != null)
            e.setPrice(dto.getPrice());
        if (dto.getStock() != null)
            e.setStock(dto.getStock());
        if (dto.getDescription() != null)
            e.setDescription(dto.getDescription());
        if (dto.getImage_url() != null)
            e.setImageUrl(dto.getImage_url());
        e.setActive(dto.getActive());
        if (dto.getCategoryId() != null) {
            CategoryEntity category = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "Không tìm thấy danh mục với ID là: " + dto.getCategoryId()));
            e.setCategory(category);
        }

        if (dto.getSupplierId() != null) {
            SupplierEntity supplier = supplierRepository.findById(dto.getSupplierId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "Không tìm thấy nhà cung cấp với ID là: " + dto.getSupplierId()));
            e.setSupplier(supplier);
        }
        return e;
    }

    // Hàm chuyển đổi entity sang dto:
    private ProductDTO toDTO(ProductEntity entity) {
        ProductDTO dto = new ProductDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setBrand(entity.getBrand());
        dto.setPrice(entity.getPrice());
        dto.setStock(entity.getStock());
        dto.setDescription(entity.getDescription());
        dto.setImage_url(entity.getImageUrl());
        dto.setActive(entity.isActive());
        if (entity.getCategory() != null)
            dto.setCategoryId(entity.getCategory().getId());
        dto.setCategoryName(entity.getCategory().getName());
        if (entity.getSupplier() != null)
            dto.setSupplierId(entity.getSupplier().getId());
        dto.setSupplierName(entity.getSupplier().getName());
        return dto;
    }

    @Override
    public void softDeleteById(Long id) {
        ProductEntity product = productRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tồn tại sản phẩm có ID là: " + id));
        if (product.isActive() == false)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Sản phẩm đã được chuyển vào thùng rác");
        product.setActive(false);
        productRepository.save(product);
    }

    @Override
    @Transactional
    public void softDeleteByIds(List<Long> ids) {
        long count = productRepository.countByIdIn(ids);
        if (count != ids.size())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Một số sản phẩm không tồn tại");
        List<ProductEntity> productsEntities = productRepository.findAllByIdIn(ids);
        for (int i = 0; i < productsEntities.size(); i++) {
            ProductEntity entity = productsEntities.get(i);
            if (entity.isActive()) {
                entity.setActive(false);
                productRepository.save(entity);
            }
        }
    }

    @Override
    public ProductDTO patchUpdate(Long id, Map<String, Object> updates) {
        ProductEntity product = productRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Không có sản phẩm nào với iD là: " + id));
        for (Map.Entry<String, Object> e : updates.entrySet()) {
            String key = e.getKey();
            Object value = e.getValue();
            if (value == null)
                continue;
            try {
                switch (key) {
                    case "name":
                        product.setName(value.toString());
                        break;
                    case "brand":
                        product.setBrand(value.toString());
                        break;
                    case "price":
                        product.setPrice(new BigDecimal(value.toString()));
                        break;
                    case "stock":
                        product.setStock(Integer.parseInt(value.toString()));
                        break;
                    case "description":
                        product.setDescription((String) value);
                        break;
                    case "image_url":
                        storageService.delete(product.getImageUrl());

                        product.setImageUrl(value.toString());
                        break;
                    case "categoryId":
                        Long newCategoryId = Long.parseLong(value.toString());
                        CategoryEntity category = categoryRepository.findById(newCategoryId).orElseThrow(
                                () -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                                        "Không tìm thấy danh mục có ID là: " + newCategoryId));
                        product.setCategory(category);
                        break;
                    case "supplierId":
                        Long newSupplierId = Long.parseLong(value.toString());
                        SupplierEntity sup = supplierRepository.findById(newSupplierId)
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                                        "Không tìm thấy nhà cung cấp có Id là: " + newSupplierId));
                        product.setSupplier(sup);
                        break;
                    default:
                        break;

                }
            } catch (NumberFormatException ex) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Giá trị của " + key + " không đúng định dạng");
            }
        }

        ProductEntity saveEntity = productRepository.save(product);
        return toDTO(saveEntity);
    }

    @Override
    public ProductDTO save(ProductDTO productDTO) {
        ProductEntity e = toEntity(productDTO);
        ProductEntity savedEntity = productRepository.save(e);
        return toDTO(savedEntity);
    }

    @Override
    public ProductDTO findById(Long id) {
        ProductEntity entity = productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy sản phẩm"));
        ProductDTO dto = toDTO(entity);
        return dto;
    }

    @Override
    public Page<ProductDTO> getAllActiveProducts(Pageable pageable) {
        Page<ProductEntity> entities = productRepository.findByActive(true, pageable);
        List<ProductDTO> dtos = new ArrayList<>();
        List<ProductEntity> content = entities.getContent();
        for (int i = 0; i < content.size(); i++) {
            dtos.add(toDTO(content.get(i)));
        }
        return new PageImpl<>(dtos, pageable, entities.getTotalElements());
    }

    public void restoreProduct(Long id) {
        ProductEntity product = productRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy sản phẩm có id là: " + id));
        if (product.isActive())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Sản phẩm này chưa bị xóa.");
        product.setActive(true);
        productRepository.save(product);
    }

    public Page<ProductDTO> getAllProductsDeleted(Pageable pageable) {
        Page<ProductEntity> pageEntities = productRepository.findByActive(false, pageable);
        List<ProductDTO> dtos = new ArrayList<>();
        List<ProductEntity> entities = pageEntities.getContent();
        for (int i = 0; i < entities.size(); i++) {
            ProductEntity e = entities.get(i);
            dtos.add(toDTO(e));
        }
        return new PageImpl<>(dtos, pageable, pageEntities.getTotalElements());
    }

    public void hardDeleteById(Long id) {
        ProductEntity product = productRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy sản phẩm có id là: " + id));
        if (product.isActive())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Sản phẩm phải ở trong thùng rác mới được xóa");
        imageService.deleteImage(product.getImageUrl());
        productRepository.deleteById(id);
    }

    public void hardDeleteByIds(List<Long> ids) {
        Long count = productRepository.countByIdIn(ids);
        if (count != ids.size())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Một số sản phẩm không tồn tại !!");
        List<ProductEntity> entities = productRepository.findAllByIdIn(ids);
        for (int i = 0; i < entities.size(); i++) {
            ProductEntity e = entities.get(i);
            imageService.deleteImage(e.getImageUrl());
            productRepository.deleteById(e.getId());
        }
    }
}
