package com.example.dangjang.common.interceptor;

import com.example.dangjang.common.annotation.Auth;
import com.example.dangjang.common.auth.AuthUser;
import com.example.dangjang.common.auth.JwtProvider;
import com.example.dangjang.common.auth.TokenBlacklistService;
import com.example.dangjang.common.exception.BusinessException;
import com.example.dangjang.common.exception.ErrorCode;
import com.example.dangjang.domain.user.entity.Role;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {

    public static final String AUTH_USER_ATTRIBUTE = "authUser";

    private static final String AUTH_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtProvider jwtProvider;
    private final TokenBlacklistService tokenBlacklistService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }

        Auth auth = resolveAuth(handlerMethod);
        if (auth == null) {
            return true;
        }

        String header = request.getHeader(AUTH_HEADER);
        if (header == null || !header.startsWith(BEARER_PREFIX)) {
            throw new BusinessException(ErrorCode.AUTH_UNAUTHORIZED);
        }

        String token = header.substring(BEARER_PREFIX.length()).trim();
        if (token.isEmpty()) {
            throw new BusinessException(ErrorCode.AUTH_UNAUTHORIZED);
        }

        JwtProvider.ParsedAccessToken parsed = jwtProvider.parseAccess(token);

        if (tokenBlacklistService.isBlacklisted(parsed.token())) {
            throw new BusinessException(ErrorCode.AUTH_INVALID_TOKEN);
        }

        Role[] allowed = auth.roles();
        if (allowed.length > 0 && !hasAllowedRole(parsed.role(), allowed)) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }

        request.setAttribute(AUTH_USER_ATTRIBUTE, new AuthUser(parsed.userId(), parsed.role()));
        return true;
    }

    private Auth resolveAuth(HandlerMethod handlerMethod) {
        Auth methodAnnotation = handlerMethod.getMethodAnnotation(Auth.class);
        if (methodAnnotation != null) {
            return methodAnnotation;
        }
        return handlerMethod.getBeanType().getAnnotation(Auth.class);
    }

    private boolean hasAllowedRole(Role userRole, Role[] allowed) {
        for (Role role : allowed) {
            if (role == userRole) {
                return true;
            }
        }
        return false;
    }
}
