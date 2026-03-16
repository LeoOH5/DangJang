package com.example.dangjang.common.interceptor;

import com.example.dangjang.common.annotation.AdminOnly;
import com.example.dangjang.common.exception.BusinessException;
import com.example.dangjang.common.exception.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AdminAuthInterceptor implements HandlerInterceptor {

    private static final String ADMIN_KEY_HEADER = "X-Admin-Key";

    @Value("${admin.secret-key}")
    private String adminSecretKey;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }

        boolean requiresAdmin =
                handlerMethod.hasMethodAnnotation(AdminOnly.class) ||
                        handlerMethod.getBeanType().isAnnotationPresent(AdminOnly.class);

        if (!requiresAdmin) {
            return true;
        }

        String adminKey = request.getHeader(ADMIN_KEY_HEADER);

        if (adminKey == null || adminKey.isBlank()) {
            throw new BusinessException(ErrorCode.MISSING_ADMIN_KEY);
        }

        if (!adminSecretKey.equals(adminKey)) {
            throw new BusinessException(ErrorCode.INVALID_ADMIN_KEY);
        }

        return true;
    }
}
