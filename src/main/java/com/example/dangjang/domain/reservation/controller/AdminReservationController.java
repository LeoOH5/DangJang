package com.example.dangjang.domain.reservation.controller;

import com.example.dangjang.common.response.ApiResponse;
import com.example.dangjang.domain.reservation.dto.AdminReservationListResponse;
import com.example.dangjang.domain.reservation.service.AdminReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
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
}
