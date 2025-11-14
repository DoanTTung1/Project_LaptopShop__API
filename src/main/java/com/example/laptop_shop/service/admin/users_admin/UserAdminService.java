package com.example.laptop_shop.service.admin.users_admin;


import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.laptop_shop.model.UserAdminDTO;
import com.example.laptop_shop.model.UserSearchCriteriaDTO;

public interface UserAdminService {

    Page<UserAdminDTO> searchAndPaginate(UserSearchCriteriaDTO criteria, Pageable pageable);
    Optional<UserAdminDTO> findById(Long id);
    Optional<UserAdminDTO> create (UserAdminDTO user);
    Optional<UserAdminDTO> update(Long id,UserAdminDTO user);
    void deleteUser(Long id);
    void deleteUsers(List<Long> ids);
}
