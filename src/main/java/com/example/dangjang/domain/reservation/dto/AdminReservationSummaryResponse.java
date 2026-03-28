package com.example.dangjang.domain.reservation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AdminReservationSummaryResponse {

    private final Long reservationId;
    private final Long userId;
    private final String userName;
    private final Long storeId;
    private final String storeName;
    private final String status;
    private final String pickupDate;
    private final String pickupTime;
}
