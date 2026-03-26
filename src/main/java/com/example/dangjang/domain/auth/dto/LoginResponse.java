package com.example.dangjang.domain.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginResponse {

    private final String accessToken;
    private final String tokenType;
    private final UserInfo user;

    @Getter
    @AllArgsConstructor
    public static class UserInfo {
        private final Long userId;
        private final String email;
        private final String name;
    }
}

