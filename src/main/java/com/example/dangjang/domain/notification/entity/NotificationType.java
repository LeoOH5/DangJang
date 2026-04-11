package com.example.dangjang.domain.notification.entity;

public enum NotificationType {
    // 예약 관련
    RESERVATION_REQUESTED,      // 사용자 → 매장
    RESERVATION_CONFIRMED,      // 매장 → 사용자
    RESERVATION_REJECTED,       // 매장 → 사용자

    // 할인 / 상품
    DISCOUNT_CREATED,           // 매장 → 사용자 (즐겨찾기 기반)

    // 시스템 알림
    PICKUP_REMINDER,            // 시스템 → 사용자
}
