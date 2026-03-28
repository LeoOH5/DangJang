package com.example.dangjang.domain.user.dto;

public record MyInfoResponse(
        Long userId,
        String email,
        String name,
        String phone
) {
}
