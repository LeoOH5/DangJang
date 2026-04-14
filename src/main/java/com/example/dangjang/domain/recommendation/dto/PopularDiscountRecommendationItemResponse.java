package com.example.dangjang.domain.recommendation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class PopularDiscountRecommendationItemResponse {

    private final int rank;
    private final Long productDiscountId;
    private final Long productId;
    private final String productName;
    private final Long storeId;
    private final String storeName;
    private final Long marketId;
    private final String marketName;
    private final BigDecimal originalPrice;
    private final BigDecimal discountPrice;
    private final Integer remainingQuantity;
    private final String status;
    private final double score;
}
