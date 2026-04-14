package com.example.dangjang.domain.recommendation.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class RecommendationSearchEventRequest {

    private List<Long> productIds;
}
