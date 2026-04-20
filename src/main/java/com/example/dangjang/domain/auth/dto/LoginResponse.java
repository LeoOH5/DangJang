package com.example.dangjang.domain.auth.dto;

import com.example.dangjang.domain.user.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginResponse {

    private final String accessToken;
    private final String refreshToken;
    private final String tokenType;
    private final UserInfo user;

    @Getter
    @AllArgsConstructor
    public static class UserInfo {
        private final Long userId;
        private final String email;
        private final String name;
        private final Role role;
    }
}
