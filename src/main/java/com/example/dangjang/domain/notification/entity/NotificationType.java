package com.example.dangjang.domain.notification.entity;

public enum NotificationType {
    // 예약 관련
    RESERVATION_REQUESTED,
    RESERVATION_CONFIRMED,
    RESERVATION_REJECTED,
    // 할인 / 상품
    DISCOUNT_CREATED,
    // 시스템 알림
    PICKUP_REMINDER,
    POPULAR_PRODUCT_RECOMMENDATION,
}
