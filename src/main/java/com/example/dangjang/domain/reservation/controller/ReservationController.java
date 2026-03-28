package com.example.dangjang.domain.reservation.controller;

import com.example.dangjang.common.response.ApiResponse;
import com.example.dangjang.domain.reservation.dto.ReservationCancelResponse;
import com.example.dangjang.domain.reservation.dto.ReservationCreateRequest;
import com.example.dangjang.domain.reservation.dto.ReservationCreateResponse;
import com.example.dangjang.domain.reservation.dto.ReservationDetailResponse;
import com.example.dangjang.domain.reservation.dto.ReservationListResponse;
import com.example.dangjang.domain.reservation.service.ReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<ReservationListResponse> getMyReservations(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        ReservationListResponse response = reservationService.getMyReservations(authorization, status, page, size);
        return ApiResponse.ok("예약 목록 조회에 성공했습니다.", response);
    }

    @GetMapping("/{reservationId}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<ReservationDetailResponse> getReservationDetail(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @PathVariable Long reservationId
    ) {
        ReservationDetailResponse response = reservationService.getReservationDetail(authorization, reservationId);
        return ApiResponse.ok("예약 상세 조회에 성공했습니다.", response);
    }

    @PatchMapping("/{reservationId}/cancel")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<ReservationCancelResponse> cancelReservation(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @PathVariable Long reservationId
    ) {
        ReservationCancelResponse response = reservationService.cancelReservation(authorization, reservationId);
        return ApiResponse.ok("예약이 취소되었습니다.", response);
    }
}
