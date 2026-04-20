package com.example.dangjang.common.auth;

import com.example.dangjang.domain.user.entity.Role;

public record AuthUser(Long userId, Role role) {
}
