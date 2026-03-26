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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
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

    @Value("${admin.secret-key}")
    private String jwtSecretKey;

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

        String accessToken = createJwtAccessToken(user);
        return new LoginResponse(
                accessToken,
                "Bearer",
                new LoginResponse.UserInfo(user.getId(), user.getEmail(), user.getName())
        );
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private String createJwtAccessToken(User user) {
        Instant now = Instant.now();
        long iat = now.getEpochSecond();
        long exp = now.plusSeconds(60 * 60 * 24).getEpochSecond();

        String headerJson = "{\"alg\":\"HS256\",\"typ\":\"JWT\"}";
        String payloadJson = String.format(
                "{\"sub\":%d,\"email\":\"%s\",\"name\":\"%s\",\"iat\":%d,\"exp\":%d}",
                user.getId(),
                escapeJson(user.getEmail()),
                escapeJson(user.getName()),
                iat,
                exp
        );

        String header = base64UrlEncode(headerJson.getBytes(StandardCharsets.UTF_8));
        String payload = base64UrlEncode(payloadJson.getBytes(StandardCharsets.UTF_8));
        String signingInput = header + "." + payload;

        byte[] signatureBytes = hmacSha256(signingInput.getBytes(StandardCharsets.UTF_8), jwtSecretKey);
        String signature = base64UrlEncode(signatureBytes);

        return signingInput + "." + signature;
    }

    private byte[] hmacSha256(byte[] data, String secretKey) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            return mac.doFinal(data);
        } catch (Exception e) {
            throw new IllegalStateException("JWT signing failed", e);
        }
    }

    private String base64UrlEncode(byte[] bytes) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String escapeJson(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}

