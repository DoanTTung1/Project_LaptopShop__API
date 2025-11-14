package com.example.laptop_shop.service.admin.users_admin;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.example.laptop_shop.model.UserAdminDTO;
import com.example.laptop_shop.model.UserSearchCriteriaDTO;
import com.example.laptop_shop.repository.UserRepository;
import com.example.laptop_shop.repository.entity.UserEntity;
import com.example.laptop_shop.specification.UserSpecification;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserAdminServiceImpl implements UserAdminService {

    private final PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;

    private UserAdminDTO toDTO(UserEntity e) {
        UserAdminDTO userDTO = new UserAdminDTO();
        userDTO.setUsername(e.getUsername());
        userDTO.setEmail(e.getEmail());
        userDTO.setRole(e.getRole());
        userDTO.setId(e.getId());
        userDTO.setCreated_at(e.getCreatedAt());
        return userDTO;
    }

    @Override
    public Optional<UserAdminDTO> create(UserAdminDTO user) {
        if (userRepository.existsByUsername(user.getUsername()))
            return Optional.empty();
        else {
            UserEntity e = new UserEntity();
            e.setUsername(user.getUsername());
            e.setEmail(user.getEmail());
            e.setRole(user.getRole());
            e.setCreatedAt(LocalDateTime.now());
            e.setPassword(passwordEncoder.encode("default123"));
            UserEntity saveEntity = userRepository.save(e);
            UserAdminDTO dtos = toDTO(saveEntity);
            return Optional.of(dtos);
        }
    }

    @Override
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Không tìm thấy người dùng có Id là " + id + " để xóa!");
        } else
            userRepository.deleteById(id);
    }

    @Override
    public Optional<UserAdminDTO> findById(Long id) {

        Optional<UserEntity> e = userRepository.findById(id);
        if (e.isPresent()) {
            UserEntity entities = e.get();
            UserAdminDTO dtos = toDTO(entities);
            return Optional.of(dtos);
        } else
            return Optional.empty();
    }

    @Override
    public Page<UserAdminDTO> searchAndPaginate(UserSearchCriteriaDTO criteria, Pageable pageable) {
        Specification<UserEntity> spec = UserSpecification.filterUser(criteria);
        Page<UserEntity> entities = userRepository.findAll(spec, pageable);
        List<UserAdminDTO> dtos = new ArrayList<>();
        for (UserEntity i : entities.getContent()) {
            dtos.add(toDTO(i));
        }
        return new PageImpl<>(dtos, pageable, entities.getTotalElements());
    }

    @Override
    public Optional<UserAdminDTO> update(Long id, UserAdminDTO user) {
        Optional<UserEntity> e = userRepository.findById(id);
        if (e.isEmpty())
            return Optional.empty();
        else {
            UserEntity entities = e.get();
            if (user.getEmail() != null)
                entities.setEmail(user.getEmail());
            if (user.getRole() != null)
                entities.setRole(user.getRole());
            UserEntity upDatedE = userRepository.save(entities);
            return Optional.of(toDTO(upDatedE));
        }
    }

    @Override
    @Transactional
    public void deleteUsers(List<Long> ids) {
        Long count = userRepository.countByIdIn(ids);
        if (count != ids.size())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Một số id người dùng không tồn tại !!");
        userRepository.deleteByIdIn(ids);
    }

}
