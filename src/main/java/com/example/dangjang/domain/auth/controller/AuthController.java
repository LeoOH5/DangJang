package com.example.dangjang.domain.auth.controller;

import com.example.dangjang.common.response.ApiResponse;
import com.example.dangjang.domain.auth.dto.LoginRequest;
import com.example.dangjang.domain.auth.dto.LoginResponse;
import com.example.dangjang.domain.auth.dto.SignUpRequest;
import com.example.dangjang.domain.auth.dto.SignUpResponse;
import com.example.dangjang.domain.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<SignUpResponse> signUp(@RequestBody SignUpRequest request) {
        SignUpResponse response = authService.signUp(request);
        return ApiResponse.ok("회원가입이 완료되었습니다.", response);
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<LoginResponse> login(@RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ApiResponse.ok("로그인에 성공했습니다.", response);
    }
}

