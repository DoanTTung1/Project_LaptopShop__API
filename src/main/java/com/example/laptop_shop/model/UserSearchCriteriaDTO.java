package com.example.laptop_shop.model;



import com.example.laptop_shop.repository.entity.Role;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserSearchCriteriaDTO {

    //Tìm kiếm theo email hoặc mật khẩu
    private String keyword;
    //Tìm kiếm theo role.
    private Role role;
}
