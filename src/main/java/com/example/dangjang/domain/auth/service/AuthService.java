package com.example.dangjang.domain.auth.service;

import com.example.dangjang.common.exception.BusinessException;
import com.example.dangjang.common.exception.ErrorCode;
import com.example.dangjang.domain.auth.dto.LoginRequest;
import com.example.dangjang.domain.auth.dto.LoginResponse;
import com.example.dangjang.domain.auth.dto.SignUpRequest;
import com.example.dangjang.domain.auth.dto.SignUpResponse;
import com.example.dangjang.domain.user.entity.User;
import com.example.dangjang.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    private static final Pattern PHONE_PATTERN =
            Pattern.compile("^\\d{2,3}-\\d{3,4}-\\d{4}$");
    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[^A-Za-z\\d]).{8,}$");

    private final UserRepository userRepository;

    @Transactional
    public SignUpResponse signUp(SignUpRequest request) {
        if (isBlank(request.getEmail())
                || isBlank(request.getPassword())
                || isBlank(request.getName())
                || isBlank(request.getPhone())) {
            throw new BusinessException(ErrorCode.REQUIRED_FIELD_MISSING);
        }

        if (!EMAIL_PATTERN.matcher(request.getEmail()).matches()
                || !PHONE_PATTERN.matcher(request.getPhone()).matches()
                || !PASSWORD_PATTERN.matcher(request.getPassword()).matches()) {
            throw new BusinessException(ErrorCode.AUTH_INVALID_INPUT);
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException(ErrorCode.AUTH_EMAIL_DUPLICATED);
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(request.getPassword())
                .name(request.getName())
                .phone(request.getPhone())
                .build();

        User saved = userRepository.save(user);
        return new SignUpResponse(saved.getId(), saved.getEmail(), saved.getName());
    }

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        if (isBlank(request.getEmail()) || isBlank(request.getPassword())) {
            throw new BusinessException(ErrorCode.REQUIRED_FIELD_MISSING);
        }

        if (!EMAIL_PATTERN.matcher(request.getEmail()).matches()) {
            throw new BusinessException(ErrorCode.AUTH_INVALID_INPUT);
        }

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BusinessException(ErrorCode.AUTH_USER_NOT_FOUND));

        if (!user.getPassword().equals(request.getPassword())) {
            throw new BusinessException(ErrorCode.AUTH_PASSWORD_MISMATCH);
        }

        return new LoginResponse(
                "jwt-access-token",
                "Bearer",
                new LoginResponse.UserInfo(user.getId(), user.getEmail(), user.getName())
        );
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}

