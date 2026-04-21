package com.example.dangjang.common.auth;

import com.example.dangjang.common.exception.BusinessException;
import com.example.dangjang.common.exception.ErrorCode;
import com.example.dangjang.domain.user.entity.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtProvider {

    private static final String ROLE_CLAIM = "role";
    private static final String TYPE_CLAIM = "typ";
    private static final String ACCESS_TYPE = "access";
    private static final String REFRESH_TYPE = "refresh";

    private final SecretKey signingKey;
    private final long accessValidityMillis;
    private final long refreshValidityMillis;

    public JwtProvider(
            @Value("${jwt.secret-key}") String secret,
            @Value("${jwt.access-token-validity-ms}") long accessValidityMillis,
            @Value("${jwt.refresh-token-validity-ms}") long refreshValidityMillis
    ) {
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessValidityMillis = accessValidityMillis;
        this.refreshValidityMillis = refreshValidityMillis;
    }

    public TokenInfo createAccessToken(Long userId, Role role) {
        Instant now = Instant.now();
        Instant exp = now.plusMillis(accessValidityMillis);
        String token = Jwts.builder()
                .id(UUID.randomUUID().toString())
                .subject(String.valueOf(userId))
                .claim(ROLE_CLAIM, role.name())
                .claim(TYPE_CLAIM, ACCESS_TYPE)
                .issuedAt(Date.from(now))
                .expiration(Date.from(exp))
                .signWith(signingKey, Jwts.SIG.HS256)
                .compact();
        return new TokenInfo(token, exp.getEpochSecond());
    }

    public TokenInfo createRefreshToken(Long userId) {
        Instant now = Instant.now();
        Instant exp = now.plusMillis(refreshValidityMillis);
        String token = Jwts.builder()
                .id(UUID.randomUUID().toString())
                .subject(String.valueOf(userId))
                .claim(TYPE_CLAIM, REFRESH_TYPE)
                .issuedAt(Date.from(now))
                .expiration(Date.from(exp))
                .signWith(signingKey, Jwts.SIG.HS256)
                .compact();
        return new TokenInfo(token, exp.getEpochSecond());
    }

    public ParsedAccessToken parseAccess(String token) {
        Claims claims = parseClaims(token, ErrorCode.AUTH_INVALID_TOKEN);
        if (!ACCESS_TYPE.equals(claims.get(TYPE_CLAIM, String.class))) {
            throw new BusinessException(ErrorCode.AUTH_INVALID_TOKEN);
        }
        try {
            Long userId = Long.valueOf(claims.getSubject());
            Role role = Role.valueOf(claims.get(ROLE_CLAIM, String.class));
            long exp = claims.getExpiration().toInstant().getEpochSecond();
            return new ParsedAccessToken(token, userId, role, exp);
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new BusinessException(ErrorCode.AUTH_INVALID_TOKEN);
        }
    }

    public ParsedRefreshToken parseRefresh(String token) {
        Claims claims = parseClaims(token, ErrorCode.AUTH_REFRESH_TOKEN_INVALID);
        if (!REFRESH_TYPE.equals(claims.get(TYPE_CLAIM, String.class))) {
            throw new BusinessException(ErrorCode.AUTH_REFRESH_TOKEN_INVALID);
        }
        try {
            Long userId = Long.valueOf(claims.getSubject());
            long exp = claims.getExpiration().toInstant().getEpochSecond();
            return new ParsedRefreshToken(token, userId, exp);
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new BusinessException(ErrorCode.AUTH_REFRESH_TOKEN_INVALID);
        }
    }

    private Claims parseClaims(String token, ErrorCode invalidCode) {
        try {
            return Jwts.parser()
                    .verifyWith(signingKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            throw new BusinessException(ErrorCode.AUTH_EXPIRED_TOKEN);
        } catch (JwtException | IllegalArgumentException e) {
            throw new BusinessException(invalidCode);
        }
    }

    public long getRefreshValidityMillis() {
        return refreshValidityMillis;
    }

    public record TokenInfo(String token, long expEpochSeconds) {
    }

    public record ParsedAccessToken(String token, Long userId, Role role, long expEpochSeconds) {
    }

    public record ParsedRefreshToken(String token, Long userId, long expEpochSeconds) {
    }
}
