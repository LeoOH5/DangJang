package com.example.dangjang.domain.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TokenPairResponse {

    private final String accessToken;
    private final String refreshToken;
    private final String tokenType;
}
