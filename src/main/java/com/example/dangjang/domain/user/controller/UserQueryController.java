package com.example.dangjang.domain.user.controller;

import com.example.dangjang.common.response.ApiResponse;
import com.example.dangjang.domain.user.dto.MyInfoResponse;
import com.example.dangjang.domain.user.service.UserQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserQueryController {

    private final UserQueryService userQueryService;

    @GetMapping("/me")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<MyInfoResponse> getMyInfo(
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        MyInfoResponse response = userQueryService.getMyInfo(authorization);
        return ApiResponse.ok("내 정보 조회에 성공했습니다.", response);
    }
}
