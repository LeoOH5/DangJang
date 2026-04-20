package com.example.dangjang.common.resolver;

import com.example.dangjang.common.annotation.LoginUser;
import com.example.dangjang.common.auth.AuthUser;
import com.example.dangjang.common.exception.BusinessException;
import com.example.dangjang.common.exception.ErrorCode;
import com.example.dangjang.common.interceptor.AuthInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class AuthUserArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(LoginUser.class)
                && parameter.getParameterType().equals(AuthUser.class);
    }

    @Override
    public Object resolveArgument(
            MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory
    ) {
        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        if (request == null) {
            throw new BusinessException(ErrorCode.AUTH_UNAUTHORIZED);
        }
        AuthUser authUser = (AuthUser) request.getAttribute(AuthInterceptor.AUTH_USER_ATTRIBUTE);
        if (authUser == null) {
            throw new BusinessException(ErrorCode.AUTH_UNAUTHORIZED);
        }
        return authUser;
    }
}