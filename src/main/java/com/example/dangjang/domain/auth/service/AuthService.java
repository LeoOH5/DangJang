package com.example.dangjang.domain.auth.service;

import com.example.dangjang.common.auth.JwtProvider;
import com.example.dangjang.common.auth.LoginRateLimiter;
import com.example.dangjang.common.auth.RefreshTokenStore;
import com.example.dangjang.common.auth.TokenBlacklistService;
import com.example.dangjang.common.exception.BusinessException;
import com.example.dangjang.common.exception.ErrorCode;
import com.example.dangjang.domain.auth.dto.LoginRequest;
import com.example.dangjang.domain.auth.dto.LoginResponse;
import com.example.dangjang.domain.auth.dto.RefreshTokenRequest;
import com.example.dangjang.domain.auth.dto.SignUpRequest;
import com.example.dangjang.domain.auth.dto.SignUpResponse;
import com.example.dangjang.domain.auth.dto.TokenPairResponse;
import com.example.dangjang.domain.user.entity.Role;
import com.example.dangjang.domain.user.entity.User;
import com.example.dangjang.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
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
    private static final String BEARER_PREFIX = "Bearer ";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final TokenBlacklistService tokenBlacklistService;
    private final RefreshTokenStore refreshTokenStore;
    private final LoginRateLimiter loginRateLimiter;

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

        Role role = request.getRole() != null ? request.getRole() : Role.USER;

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .phone(request.getPhone())
                .role(role)
                .build();

        User saved = userRepository.save(user);
        return new SignUpResponse(saved.getId(), saved.getEmail(), saved.getName());
    }

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request, String clientIp) {
        if (isBlank(request.getEmail()) || isBlank(request.getPassword())) {
            throw new BusinessException(ErrorCode.REQUIRED_FIELD_MISSING);
        }

        if (!EMAIL_PATTERN.matcher(request.getEmail()).matches()) {
            throw new BusinessException(ErrorCode.AUTH_INVALID_INPUT);
        }

        loginRateLimiter.checkAndIncrement(clientIp, request.getEmail());

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BusinessException(ErrorCode.AUTH_USER_NOT_FOUND));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException(ErrorCode.AUTH_PASSWORD_MISMATCH);
        }

        loginRateLimiter.reset(clientIp, request.getEmail());

        JwtProvider.TokenInfo access = jwtProvider.createAccessToken(user.getId(), user.getRole());
        JwtProvider.TokenInfo refresh = jwtProvider.createRefreshToken(user.getId());
        refreshTokenStore.save(user.getId(), refresh.token(), Duration.ofMillis(jwtProvider.getRefreshValidityMillis()));

        return new LoginResponse(
                access.token(),
                refresh.token(),
                "Bearer",
                new LoginResponse.UserInfo(user.getId(), user.getEmail(), user.getName(), user.getRole())
        );
    }

    @Transactional(readOnly = true)
    public TokenPairResponse refresh(RefreshTokenRequest request) {
        if (request == null || isBlank(request.getRefreshToken())) {
            throw new BusinessException(ErrorCode.AUTH_REFRESH_TOKEN_INVALID);
        }
        JwtProvider.ParsedRefreshToken parsed = jwtProvider.parseRefresh(request.getRefreshToken());

        if (!refreshTokenStore.matches(parsed.userId(), parsed.token())) {
            throw new BusinessException(ErrorCode.AUTH_REFRESH_TOKEN_INVALID);
        }

        User user = userRepository.findById(parsed.userId())
                .orElseThrow(() -> new BusinessException(ErrorCode.AUTH_USER_NOT_FOUND));

        JwtProvider.TokenInfo access = jwtProvider.createAccessToken(user.getId(), user.getRole());
        JwtProvider.TokenInfo refresh = jwtProvider.createRefreshToken(user.getId());
        refreshTokenStore.save(user.getId(), refresh.token(), Duration.ofMillis(jwtProvider.getRefreshValidityMillis()));

        return new TokenPairResponse(access.token(), refresh.token(), "Bearer");
    }

    @Transactional
    public void logout(String authorization) {
        String token = extractBearerToken(authorization);
        JwtProvider.ParsedAccessToken parsed = jwtProvider.parseAccess(token);

        if (tokenBlacklistService.isBlacklisted(parsed.token())) {
            throw new BusinessException(ErrorCode.AUTH_INVALID_TOKEN);
        }
        tokenBlacklistService.blacklist(parsed.token(), parsed.expEpochSeconds());
        refreshTokenStore.revoke(parsed.userId());
    }

    @Transactional(readOnly = true)
    public Long getAuthenticatedUserId(String authorization) {
        String token = extractBearerToken(authorization);
        JwtProvider.ParsedAccessToken parsed = jwtProvider.parseAccess(token);
        if (tokenBlacklistService.isBlacklisted(parsed.token())) {
            throw new BusinessException(ErrorCode.AUTH_INVALID_TOKEN);
        }
        return parsed.userId();
    }

    private String extractBearerToken(String authorization) {
        if (authorization == null || authorization.isBlank()) {
            throw new BusinessException(ErrorCode.AUTH_UNAUTHORIZED);
        }
        if (!authorization.startsWith(BEARER_PREFIX)) {
            throw new BusinessException(ErrorCode.AUTH_INVALID_TOKEN);
        }
        String token = authorization.substring(BEARER_PREFIX.length()).trim();
        if (token.isEmpty()) {
            throw new BusinessException(ErrorCode.AUTH_INVALID_TOKEN);
        }
        return token;
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
