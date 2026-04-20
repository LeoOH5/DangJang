package com.example.dangjang.domain.auth.dto;

import com.example.dangjang.domain.user.entity.Role;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SignUpRequest {

    private String email;
    private String password;
    private String name;
    private String phone;
    private Role role;
}