package com.example.dangjang.domain.review.controller;

import com.example.dangjang.common.response.ApiResponse;
import com.example.dangjang.domain.review.dto.ReviewCreateRequest;
import com.example.dangjang.domain.review.dto.ReviewCreateResponse;
import com.example.dangjang.domain.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/stores")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("/{storeId}/reviews")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<ReviewCreateResponse> createReview(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @PathVariable Long storeId,
            @RequestBody ReviewCreateRequest request
    ) {
        ReviewCreateResponse response = reviewService.createReview(authorization, storeId, request);
        return ApiResponse.ok("리뷰가 등록되었습니다.", response);
    }
}

