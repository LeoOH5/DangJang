package com.example.dangjang.domain.notification.controller;

import com.example.dangjang.common.exception.BusinessException;
import com.example.dangjang.common.exception.ErrorCode;
import com.example.dangjang.domain.auth.service.AuthService;
import com.example.dangjang.domain.notification.service.NotificationSseService;
import com.example.dangjang.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final AuthService authService;
    private final UserRepository userRepository;
    private final NotificationSseService notificationSseService;

    /**
     * 알림 SSE 구독. {@code Authorization: Bearer &lt;accessToken&gt;} 필수.
     * 응답 {@code Content-Type: text/event-stream}
     */
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
