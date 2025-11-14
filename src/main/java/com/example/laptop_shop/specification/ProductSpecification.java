package com.example.laptop_shop.specification;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.example.laptop_shop.model.SearchCriteriaDTO;
import com.example.laptop_shop.repository.entity.ProductEntity;

import jakarta.persistence.criteria.Predicate;

public class ProductSpecification {
    public static Specification<ProductEntity> filterProducts(SearchCriteriaDTO criteria) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            // 1 Tìm theo tên hoặc mô tả
            if (criteria.getNameOrDescription() != null && !criteria.getNameOrDescription().trim().isEmpty()) {
                String likePartern = "%" + criteria.getNameOrDescription().toLowerCase() + "%";
                Predicate preName = criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), likePartern);
                Predicate preDes = criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), likePartern);
                predicates.add(criteriaBuilder.or(preName, preDes));
            }
            // 2 Tìm kiếm theo thương hiệu
            if (criteria.getBrand() != null && !criteria.getBrand().trim().isEmpty()) {
                String likePattern = "%" + criteria.getBrand().toLowerCase() + "%";
                Predicate preBrand = criteriaBuilder.like(criteriaBuilder.lower(root.get("brand")), likePattern);
                predicates.add(preBrand);
            }
            // 3 tìm kiếm theo tên danh mục
            if (criteria.getCategoryId() != null) {
                Predicate preCateId = criteriaBuilder.equal(root.get("category").get("id"), criteria.getCategoryId());
                predicates.add(preCateId);
            }
            // 4. Tìm kiếm theo khoảng giá
            if (criteria.getFromPrice() != null) {
                Predicate preFromPrice = criteriaBuilder.greaterThanOrEqualTo(root.get("price"),
                        criteria.getFromPrice());
                predicates.add(preFromPrice);
            }
            if (criteria.getToPrice() != null) {
                Predicate preToPrice = criteriaBuilder.lessThanOrEqualTo(root.get("price"), criteria.getToPrice());
                predicates.add(preToPrice);
            }
            // 5. Tìm kiếm theo tồn kho
            if (criteria.getMinStock() != null && criteria.getMinStock() >= 0) {
                Predicate preMinStock = criteriaBuilder.greaterThanOrEqualTo(root.get("stock"), criteria.getMinStock());
                predicates.add(preMinStock);
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
