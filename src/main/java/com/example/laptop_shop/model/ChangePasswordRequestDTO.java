package com.example.laptop_shop.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ChangePasswordRequestDTO {
    String currentPassword;
    String newPassword;
    String confirmationPassword;
}
