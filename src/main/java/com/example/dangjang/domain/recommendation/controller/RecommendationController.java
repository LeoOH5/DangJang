package com.example.dangjang.domain.recommendation.controller;

import com.example.dangjang.common.response.ApiResponse;
import com.example.dangjang.domain.recommendation.dto.PopularDiscountRecommendationResponse;
import com.example.dangjang.domain.recommendation.service.RecommendationQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/recommendations")
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationQueryService recommendationQueryService;

    @GetMapping("/popular-discount-products")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<PopularDiscountRecommendationResponse> getPopularDiscountProducts(
            @RequestParam(defaultValue = "10") int size
    ) {
        PopularDiscountRecommendationResponse response = recommendationQueryService.getPopularDiscountProducts(size);
        return ApiResponse.ok("인기 할인 상품 추천 조회에 성공했습니다", response);
    }
}
