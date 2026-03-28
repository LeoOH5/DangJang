package com.example.dangjang.domain.reservation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class ReservationCreateResponse {
    private final Long reservationId;
    private final String status;
    private final Long storeId;
    private final LocalDate pickupDate;
    private final LocalTime pickupTime;
    private final List<ReservationItemResponse> items;

    @Getter
    @AllArgsConstructor
    public static class ReservationItemResponse {
        private final Long reservationItemId;
        private final Long productId;
        private final String productName;
        private final Integer quantity;
        private final BigDecimal discountPrice;
    }
}
