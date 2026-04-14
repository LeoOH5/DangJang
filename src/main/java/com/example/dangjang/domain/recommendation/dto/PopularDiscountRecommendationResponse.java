package com.example.dangjang.domain.recommendation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class PopularDiscountRecommendationResponse {

    private final String date;
    private final List<PopularDiscountRecommendationItemResponse> content;
}
