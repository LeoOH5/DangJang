package com.example.dangjang.domain.market.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class MarketListResponse {

    private final List<MarketSummaryResponse> content;
    private final int page;
    private final int size;
    private final long totalElements;
    private final int totalPages;
}

