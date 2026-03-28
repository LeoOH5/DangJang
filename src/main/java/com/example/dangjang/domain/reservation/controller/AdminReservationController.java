package com.example.dangjang.domain.reservation.controller;

import com.example.dangjang.common.response.ApiResponse;
import com.example.dangjang.domain.reservation.dto.AdminReservationListResponse;
import com.example.dangjang.domain.reservation.dto.ReservationConfirmResponse;
import com.example.dangjang.domain.reservation.dto.ReservationRejectRequest;
import com.example.dangjang.domain.reservation.dto.ReservationRejectResponse;
import com.example.dangjang.domain.reservation.service.AdminReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/reservations")
@RequiredArgsConstructor
public class AdminReservationController {

    private final AdminReservationService adminReservationService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<AdminReservationListResponse> getStoreReservations(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @RequestParam(required = false) Long storeId,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        AdminReservationListResponse response = adminReservationService.getStoreReservations(
                authorization,
                storeId,
                status,
                page,
                size
        );
        return ApiResponse.ok("예약 목록 조회에 성공했습니다.", response);
    }

    @PatchMapping("/{reservationId}/confirm")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<ReservationConfirmResponse> confirmReservation(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @PathVariable Long reservationId
    ) {
        ReservationConfirmResponse response = adminReservationService.confirmReservation(
                authorization,
                reservationId
        );
        return ApiResponse.ok("예약이 승인되었습니다.", response);
    }

    @PatchMapping("/{reservationId}/reject")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<ReservationRejectResponse> rejectReservation(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @PathVariable Long reservationId,
            @RequestBody ReservationRejectRequest request
    ) {
        ReservationRejectResponse response = adminReservationService.rejectReservation(
                authorization,
                reservationId,
                request
        );
        return ApiResponse.ok("예약이 거절되었습니다.", response);
    }
}
