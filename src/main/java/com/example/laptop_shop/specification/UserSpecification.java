package com.example.laptop_shop.specification;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.example.laptop_shop.model.UserSearchCriteriaDTO;
import com.example.laptop_shop.repository.entity.UserEntity;

import jakarta.persistence.criteria.Predicate;

public class UserSpecification {
    public static Specification<UserEntity> filterUser(UserSearchCriteriaDTO criteria) {
        return (root, query, criteriaBuilder) -> {

            List<Predicate> predicate = new ArrayList<>();
            if (criteria.getKeyword() != null && !criteria.getKeyword().trim().isEmpty()) {
                String likePattern = "%" + criteria.getKeyword().toLowerCase() + "%";
                Predicate preUserName = criteriaBuilder.like(criteriaBuilder.lower(root.get("username")), likePattern);

                Predicate preEmail = criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), likePattern);
                predicate.add(criteriaBuilder.or(preUserName, preEmail));
            }
            if (criteria.getRole() != null) {
                Predicate preRole = criteriaBuilder.equal(root.get("role"), criteria.getRole());
                predicate.add(preRole);
            }
            return criteriaBuilder.and(predicate.toArray(new Predicate[0]));
        };
    }
}
