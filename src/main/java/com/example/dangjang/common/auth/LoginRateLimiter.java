package com.example.dangjang.common.auth;

import com.example.dangjang.common.exception.BusinessException;
import com.example.dangjang.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class LoginRateLimiter {

    private static final String KEY_PREFIX = "rate:login:";
    private static final int MAX_ATTEMPTS = 10;
    private static final Duration WINDOW = Duration.ofMinutes(5);

    private final StringRedisTemplate redis;

    public void checkAndIncrement(String clientIp, String email) {
        String key = buildKey(clientIp, email);
        Long attempts = redis.opsForValue().increment(key);
        if (attempts != null && attempts == 1L) {
            redis.expire(key, WINDOW);
        }
        if (attempts != null && attempts > MAX_ATTEMPTS) {
            throw new BusinessException(ErrorCode.AUTH_RATE_LIMIT_EXCEEDED);
        }
    }

    public void reset(String clientIp, String email) {
        redis.delete(buildKey(clientIp, email));
    }

    private String buildKey(String clientIp, String email) {
        return KEY_PREFIX + clientIp + ":" + (email == null ? "" : email.toLowerCase());
    }
}
