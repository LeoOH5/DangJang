package com.example.dangjang.domain.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class NotificationUnreadCountResponse {

    private final long unreadCount;
}
