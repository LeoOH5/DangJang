package com.example.dangjang.domain.reservation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ReservationListResponse {

    private final List<ReservationSummaryResponse> content;
    private final int page;
    private final int size;
    private final long totalElements;
    private final int totalPages;
}
