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
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.time.Instant;
import java.util.Base64;
import java.util.concurrent.TimeUnit;
import java.security.MessageDigest;
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
    private static final Pattern EXP_PATTERN = Pattern.compile("\"exp\"\\s*:\\s*(\\d+)");

    @Value("${admin.secret-key}")
    private String jwtSecretKey;

    private final UserRepository userRepository;
    private final StringRedisTemplate stringRedisTemplate;

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

    @Transactional
    public void logout(String authorization) {
        AuthTokenInfo tokenInfo = parseAndValidateAccessToken(authorization);
        String token = tokenInfo.token();
        long exp = tokenInfo.exp();
        String blacklistKey = buildBlacklistKey(token);

        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(blacklistKey))) {
            throw new BusinessException(ErrorCode.AUTH_INVALID_TOKEN);
        }

        long now = Instant.now().getEpochSecond();
        long ttlSeconds = exp - now;
        if (ttlSeconds <= 0) {
            throw new BusinessException(ErrorCode.AUTH_INVALID_TOKEN);
        }

        stringRedisTemplate.opsForValue().set(blacklistKey, "1", ttlSeconds, TimeUnit.SECONDS);
    }

    @Transactional(readOnly = true)
    public Long getAuthenticatedUserId(String authorization) {
        AuthTokenInfo tokenInfo = parseAndValidateAccessToken(authorization);
        String blacklistKey = buildBlacklistKey(tokenInfo.token());
        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(blacklistKey))) {
            throw new BusinessException(ErrorCode.AUTH_INVALID_TOKEN);
        }
        return tokenInfo.userId();
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

    private String extractBearerToken(String authorization) {
        if (authorization == null || authorization.isBlank()) {
            throw new BusinessException(ErrorCode.AUTH_UNAUTHORIZED);
        }

        String prefix = "Bearer ";
        if (!authorization.startsWith(prefix)) {
            throw new BusinessException(ErrorCode.AUTH_INVALID_TOKEN);
        }

        String token = authorization.substring(prefix.length()).trim();
        if (token.isBlank()) {
            throw new BusinessException(ErrorCode.AUTH_INVALID_TOKEN);
        }

        return token;
    }

    private AuthTokenInfo parseAndValidateAccessToken(String authorization) {
        String token = extractBearerToken(authorization);
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                throw new BusinessException(ErrorCode.AUTH_INVALID_TOKEN);
            }

            String headerPart = parts[0];
            String payloadPart = parts[1];
            String signaturePart = parts[2];

            String signingInput = headerPart + "." + payloadPart;
            byte[] signatureBytes =
                    hmacSha256(signingInput.getBytes(StandardCharsets.UTF_8), jwtSecretKey);
            String computedSignaturePart = base64UrlEncode(signatureBytes);
            if (!computedSignaturePart.equals(signaturePart)) {
                throw new BusinessException(ErrorCode.AUTH_INVALID_TOKEN);
            }

            String payloadJson = new String(
                    Base64.getUrlDecoder().decode(payloadPart),
                    StandardCharsets.UTF_8
            );

            long userId = extractLongClaim(payloadJson, "sub");
            var matcher = EXP_PATTERN.matcher(payloadJson);
            if (!matcher.find()) {
                throw new BusinessException(ErrorCode.AUTH_INVALID_TOKEN);
            }

            long exp = Long.parseLong(matcher.group(1));
            long now = Instant.now().getEpochSecond();
            if (exp <= now) {
                throw new BusinessException(ErrorCode.AUTH_EXPIRED_TOKEN);
            }

            return new AuthTokenInfo(token, userId, exp);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.AUTH_INVALID_TOKEN);
        }
    }

    private long extractLongClaim(String payloadJson, String claim) {
        Pattern pattern = Pattern.compile("\"" + claim + "\"\\s*:\\s*(\\d+)");
        var matcher = pattern.matcher(payloadJson);
        if (!matcher.find()) {
            throw new BusinessException(ErrorCode.AUTH_INVALID_TOKEN);
        }
        return Long.parseLong(matcher.group(1));
    }

    private String buildBlacklistKey(String token) {
        return "auth:blacklist:" + sha256Hex(token);
    }

    private String sha256Hex(String value) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(value.getBytes(StandardCharsets.UTF_8));
            char[] hexChars = new char[digest.length * 2];
            char[] hexArray = "0123456789abcdef".toCharArray();
            for (int i = 0; i < digest.length; i++) {
                int v = digest[i] & 0xFF;
                hexChars[i * 2] = hexArray[v >>> 4];
                hexChars[i * 2 + 1] = hexArray[v & 0x0F];
            }
            return new String(hexChars);
        } catch (Exception e) {
            throw new IllegalStateException("SHA-256 failed", e);
        }
    }

    private record AuthTokenInfo(String token, Long userId, Long exp) {
    }
}

