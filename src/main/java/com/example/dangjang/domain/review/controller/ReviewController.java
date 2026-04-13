package com.example.dangjang.domain.review.controller;

import com.example.dangjang.common.response.ApiResponse;
import com.example.dangjang.domain.review.dto.ReviewCreateRequest;
import com.example.dangjang.domain.review.dto.ReviewCreateResponse;
import com.example.dangjang.domain.review.dto.ReviewUpdateRequest;
import com.example.dangjang.domain.review.dto.ReviewUpdateResponse;
import com.example.dangjang.domain.review.dto.MyReviewListResponse;
import com.example.dangjang.domain.review.dto.StoreReviewListResponse;
import com.example.dangjang.domain.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping("/reviews/me")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<MyReviewListResponse> getMyReviews(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        MyReviewListResponse response = reviewService.getMyReviews(authorization, page, size);
        return ApiResponse.ok("내 리뷰 목록 조회에 성공했습니다.", response);
    }

    @GetMapping("/stores/{storeId}/reviews")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<StoreReviewListResponse> getStoreReviews(
            @PathVariable Long storeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        StoreReviewListResponse response = reviewService.getStoreReviews(storeId, page, size);
        return ApiResponse.ok("리뷰 목록 조회에 성공했습니다.", response);
    }

    @PostMapping("/stores/{storeId}/reviews")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<ReviewCreateResponse> createReview(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @PathVariable Long storeId,
            @RequestBody ReviewCreateRequest request
    ) {
        ReviewCreateResponse response = reviewService.createReview(authorization, storeId, request);
        return ApiResponse.ok("리뷰가 등록되었습니다.", response);
    }

    @PatchMapping("/reviews/{reviewId}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<ReviewUpdateResponse> updateReview(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @PathVariable Long reviewId,
            @RequestBody ReviewUpdateRequest request
    ) {
        ReviewUpdateResponse response = reviewService.updateReview(authorization, reviewId, request);
        return ApiResponse.ok("리뷰가 수정되었습니다.", response);
    }

    @DeleteMapping("/reviews/{reviewId}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<Void> deleteReview(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @PathVariable Long reviewId
    ) {
        reviewService.deleteReview(authorization, reviewId);
        return ApiResponse.ok("리뷰가 삭제되었습니다.");
    }
}

