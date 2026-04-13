package com.example.dangjang.domain.review.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReviewUpdateResponse {

    private final Long reviewId;
    private final Integer rating;
    private final String content;
}

