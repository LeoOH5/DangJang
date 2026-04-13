package com.example.dangjang.domain.review.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@AllArgsConstructor
public class StoreReviewListResponse {

    private final BigDecimal averageRating;
    private final long reviewCount;
    private final List<StoreReviewSummaryResponse> content;
    private final int page;
    private final int size;
    private final long totalElements;
    private final int totalPages;
}
