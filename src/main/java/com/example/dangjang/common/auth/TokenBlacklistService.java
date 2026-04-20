package com.example.dangjang.common.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class TokenBlacklistService {

    private static final String PREFIX = "auth:blacklist:";

    private final StringRedisTemplate stringRedisTemplate;

    public boolean isBlacklisted(String token) {
        return Boolean.TRUE.equals(stringRedisTemplate.hasKey(key(token)));
    }

    public void blacklist(String token, long expEpochSeconds) {
        long ttl = expEpochSeconds - Instant.now().getEpochSecond();
        if (ttl <= 0) {
            return;
        }
        stringRedisTemplate.opsForValue().set(key(token), "1", ttl, TimeUnit.SECONDS);
    }

    private String key(String token) {
        return PREFIX + sha256Hex(token);
    }

    private String sha256Hex(String value) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(value.getBytes(StandardCharsets.UTF_8));
            char[] hex = new char[digest.length * 2];
            char[] map = "0123456789abcdef".toCharArray();
            for (int i = 0; i < digest.length; i++) {
                int v = digest[i] & 0xFF;
                hex[i * 2] = map[v >>> 4];
                hex[i * 2 + 1] = map[v & 0x0F];
            }
            return new String(hex);
        } catch (Exception e) {
            throw new IllegalStateException("SHA-256 failed", e);
        }
    }
}
