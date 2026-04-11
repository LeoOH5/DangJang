package com.example.dangjang.domain.notification.controller;

import com.example.dangjang.common.exception.BusinessException;
import com.example.dangjang.common.exception.ErrorCode;
import com.example.dangjang.common.response.ApiResponse;
import com.example.dangjang.domain.auth.service.AuthService;
import com.example.dangjang.domain.notification.dto.NotificationListResponse;
import com.example.dangjang.domain.notification.service.NotificationService;
import com.example.dangjang.domain.notification.service.NotificationSseService;
import com.example.dangjang.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final AuthService authService;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final NotificationSseService notificationSseService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<NotificationListResponse> getMyNotifications(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        NotificationListResponse response = notificationService.getMyNotifications(authorization, page, size);
        return ApiResponse.ok("알림 목록 조회에 성공했습니다.", response);
    }


    @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        Long userId = authService.getAuthenticatedUserId(authorization);
        userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.AUTH_USER_NOT_FOUND));

        return notificationSseService.subscribe(userId);
    }
}
