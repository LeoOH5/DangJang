package com.example.dangjang.domain.review.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class StoreReviewSummaryResponse {

    private final Long reviewId;
    private final String userName;
    private final Integer rating;
    private final String content;
    private final String createdAt;
}
