package com.example.dangjang.domain.reservation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ReservationDetailResponse {

    private final Long reservationId;
    private final String status;
    private final Long storeId;
    private final String storeName;
    private final String pickupDate;
    private final String pickupTime;
    private final String requestNote;
    private final List<ReservationDetailItemResponse> items;

    @Getter
    @AllArgsConstructor
    public static class ReservationDetailItemResponse {
        private final Long reservationItemId;
        private final Long productId;
        private final String productName;
        private final Integer quantity;
        private final Integer originalPrice;
        private final Integer discountPrice;
    }
}
