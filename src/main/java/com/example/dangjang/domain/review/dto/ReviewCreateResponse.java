package com.example.dangjang.domain.review.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReviewCreateResponse {

    private final Long reviewId;
    private final Long storeId;
    private final Long reservationId;
    private final Integer rating;
    private final String content;
}

