package com.example.dangjang.domain.reservation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReservationConfirmResponse {

    private final Long reservationId;
    private final String status;
}
