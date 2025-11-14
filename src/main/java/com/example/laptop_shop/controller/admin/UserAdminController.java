package com.example.laptop_shop.controller.admin;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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

import com.example.laptop_shop.model.UserAdminDTO;
import com.example.laptop_shop.model.UserSearchCriteriaDTO;
import com.example.laptop_shop.service.admin.users_admin.UserAdminService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class UserAdminController {
    private final UserAdminService userAdminService;

    @GetMapping
    public Page<UserAdminDTO> searchUser(UserSearchCriteriaDTO criteriaDTO,
            @PageableDefault(size = 10, sort = "username") Pageable pageable) {
        return userAdminService.searchAndPaginate(criteriaDTO, pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        var result = userAdminService.findById(id);
        if (result.isPresent())
            return new ResponseEntity<>(result.get(), HttpStatus.OK);
        else
            return new ResponseEntity<>("Không tìm thấy User có Id là: " + id, HttpStatus.NOT_FOUND);
    }

    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody UserAdminDTO userDTO) {
        var result = userAdminService.create(userDTO);
        if (result.isPresent()) {
            return new ResponseEntity<>(result.get(), HttpStatus.CREATED);
        } else
            return new ResponseEntity<>("Tên đăng nhập đã tồn tại!", HttpStatus.BAD_REQUEST);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        userAdminService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<?> deleteUsers(@RequestBody List<Long> ids) {
        if (ids == null || ids.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Vui lòng không để trống id cần xóa");
        userAdminService.deleteUsers(ids);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> updatedUser(@PathVariable Long id, @RequestBody UserAdminDTO userDTO) {
        if (userDTO.getId() == null || !id.equals(userDTO.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Id không khớp");
        } else {
            var result = userAdminService.update(id, userDTO);
            if (result.isPresent())
                return new ResponseEntity<>(result.get(), HttpStatus.OK);
            else
                return new ResponseEntity<>("Không tìm thấy User có Id là: " + id, HttpStatus.NOT_FOUND);
        }
    }
}
