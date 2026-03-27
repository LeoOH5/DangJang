package com.example.dangjang.domain.reservation.controller;

import com.example.dangjang.common.response.ApiResponse;
import com.example.dangjang.domain.reservation.dto.ReservationCreateRequest;
import com.example.dangjang.domain.reservation.dto.ReservationCreateResponse;
import com.example.dangjang.domain.reservation.service.ReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<ReservationCreateResponse> createReservation(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @Valid @RequestBody ReservationCreateRequest request
    ) {
        ReservationCreateResponse response = reservationService.createReservation(authorization, request);
        return ApiResponse.ok("예약 요청이 완료되었습니다.", response);
    }
}
