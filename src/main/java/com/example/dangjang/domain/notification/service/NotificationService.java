package com.example.dangjang.domain.notification.service;

import com.example.dangjang.common.exception.BusinessException;
import com.example.dangjang.common.exception.ErrorCode;
import com.example.dangjang.domain.auth.service.AuthService;
import com.example.dangjang.domain.notification.dto.NotificationItemResponse;
import com.example.dangjang.domain.notification.dto.NotificationListResponse;
import com.example.dangjang.domain.notification.dto.NotificationReadResponse;
import com.example.dangjang.domain.notification.entity.Notification;
import com.example.dangjang.domain.notification.repository.NotificationRepository;
import com.example.dangjang.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final AuthService authService;
    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;

    @Transactional(readOnly = true)
    public NotificationListResponse getMyNotifications(String authorization, int page, int size) {
        Long userId = authService.getAuthenticatedUserId(authorization);
        userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.AUTH_USER_NOT_FOUND));

        int safePage = Math.max(0, page);
        int safeSize = Math.min(100, Math.max(1, size));

        var pageable = PageRequest.of(safePage, safeSize);
        var result = notificationRepository.findByUser_IdOrderByCreatedAtDesc(userId, pageable);

        var content = result.getContent().stream()
                .map(this::toItem)
                .toList();

        return new NotificationListResponse(
                content,
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages()
        );
    }

    @Transactional
    public NotificationReadResponse markAsRead(String authorization, Long notificationId) {
        Long userId = authService.getAuthenticatedUserId(authorization);
        userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.AUTH_USER_NOT_FOUND));

        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOTIFICATION_NOT_FOUND));

        if (!notification.getUser().getId().equals(userId)) {
            throw new BusinessException(ErrorCode.NOTIFICATION_ACCESS_DENIED);
        }

        if (notification.isRead()) {
            throw new BusinessException(ErrorCode.NOTIFICATION_ALREADY_READ);
        }

        notification.markAsRead();
        return new NotificationReadResponse(notification.getId(), true);
    }

    @Transactional
    public void markAllAsRead(String authorization) {
        Long userId = authService.getAuthenticatedUserId(authorization);
        userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.AUTH_USER_NOT_FOUND));
        notificationRepository.markAllAsReadByUserId(userId);
    }

    private NotificationItemResponse toItem(Notification n) {
        return new NotificationItemResponse(
                n.getId(),
                n.getType().name(),
                n.getTitle(),
                n.getContent(),
                n.getTargetId(),
                n.getTargetType() != null ? n.getTargetType().name() : null,
                n.isRead(),
                n.getCreatedAt().toString()
        );
    }
}
