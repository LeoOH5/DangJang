package com.example.dangjang.domain.review.dto;

import lombok.Getter;

@Getter
public class ReviewCreateRequest {

    private Long reservationId;
    private Integer rating;
    private String content;
}

