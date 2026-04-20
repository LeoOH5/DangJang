package com.example.dangjang.common.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Duration;

@Component
@RequiredArgsConstructor
public class RefreshTokenStore {

    private static final String PREFIX = "refresh:";

    private final StringRedisTemplate redis;

    public void save(Long userId, String token, Duration ttl) {
        redis.opsForValue().set(key(userId), hash(token), ttl);
    }

    public boolean matches(Long userId, String token) {
        String stored = redis.opsForValue().get(key(userId));
        return stored != null && stored.equals(hash(token));
    }

    public void revoke(Long userId) {
        redis.delete(key(userId));
    }

    private String key(Long userId) {
        return PREFIX + userId;
    }

    private String hash(String token) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(token.getBytes(StandardCharsets.UTF_8));
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
