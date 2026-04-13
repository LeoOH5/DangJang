package com.example.dangjang.domain.notification.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class NotificationReadResponse {

    private final Long notificationId;

    @JsonProperty("isRead")
    private final boolean read;
}
