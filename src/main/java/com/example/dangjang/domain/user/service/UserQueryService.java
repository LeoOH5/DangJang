package com.example.dangjang.domain.user.service;

import com.example.dangjang.common.exception.BusinessException;
import com.example.dangjang.common.exception.ErrorCode;
import com.example.dangjang.domain.auth.service.AuthService;
import com.example.dangjang.domain.user.dto.MyInfoResponse;
import com.example.dangjang.domain.user.entity.User;
import com.example.dangjang.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserQueryService {

    private final AuthService authService;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public MyInfoResponse getMyInfo(String authorization) {
        Long userId = authService.getAuthenticatedUserId(authorization);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.AUTH_USER_NOT_FOUND));

        return new MyInfoResponse(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getPhone()
        );
    }
}
