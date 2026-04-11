package com.example.dangjang.domain.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class NotificationItemResponse {

    private final Long notificationId;
    private final String type;
    private final String title;
    private final String content;
    private final Long targetId;
    private final String targetType;
    private final boolean isRead;
    private final String createdAt;
}
