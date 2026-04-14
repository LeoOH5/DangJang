package com.example.dangjang.domain.recommendation.controller;

import com.example.dangjang.common.response.ApiResponse;
import com.example.dangjang.domain.recommendation.dto.RecommendationSearchEventRequest;
import com.example.dangjang.domain.recommendation.dto.RecommendationViewEventRequest;
import com.example.dangjang.domain.recommendation.service.RecommendationScoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/recommendations/events")
@RequiredArgsConstructor
public class RecommendationEventController {

    private final RecommendationScoreService recommendationScoreService;

    @PostMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<Void> recordSearchEvent(@RequestBody RecommendationSearchEventRequest request) {
        recommendationScoreService.recordSearchEvent(request.getProductIds());
        return ApiResponse.ok("인기 상품 검색 이벤트를 기록했습니다.");
    }

    @PostMapping("/view")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<Void> recordViewEvent(@RequestBody RecommendationViewEventRequest request) {
        recommendationScoreService.recordViewEvent(request.getProductId());
        return ApiResponse.ok("할인 상품 조회 이벤트를 기록했습니다.");
    }
}
