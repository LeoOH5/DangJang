package com.example.dangjang.domain.review.service;

import com.example.dangjang.common.exception.BusinessException;
import com.example.dangjang.common.exception.ErrorCode;
import com.example.dangjang.domain.auth.service.AuthService;
import com.example.dangjang.domain.reservation.entity.Reservation;
import com.example.dangjang.domain.reservation.repository.ReservationRepository;
import com.example.dangjang.domain.review.dto.ReviewCreateRequest;
import com.example.dangjang.domain.review.dto.ReviewCreateResponse;
import com.example.dangjang.domain.review.dto.MyReviewListResponse;
import com.example.dangjang.domain.review.dto.MyReviewSummaryResponse;
import com.example.dangjang.domain.review.dto.StoreReviewListResponse;
import com.example.dangjang.domain.review.dto.StoreReviewSummaryResponse;
import com.example.dangjang.domain.review.dto.ReviewUpdateRequest;
import com.example.dangjang.domain.review.dto.ReviewUpdateResponse;
import com.example.dangjang.domain.review.entity.Review;
import com.example.dangjang.domain.review.repository.ReviewRepository;
import com.example.dangjang.domain.store.entity.Store;
import com.example.dangjang.domain.store.repository.StoreRepository;
import com.example.dangjang.domain.user.entity.User;
import com.example.dangjang.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final AuthService authService;
    private final UserRepository userRepository;
    private final StoreRepository storeRepository;
    private final ReservationRepository reservationRepository;
    private final ReviewRepository reviewRepository;

    @Transactional
    public ReviewCreateResponse createReview(String authorization, Long storeId, ReviewCreateRequest request) {
        if (request == null
                || request.getReservationId() == null
                || request.getRating() == null
                || request.getContent() == null
                || request.getContent().isBlank()) {
            throw new BusinessException(ErrorCode.REQUIRED_FIELD_MISSING);
        }

        Long userId = authService.getAuthenticatedUserId(authorization);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.AUTH_USER_NOT_FOUND));

        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new BusinessException(ErrorCode.STORE_NOT_FOUND));

        Reservation reservation = reservationRepository.findById(request.getReservationId())
                .orElseThrow(() -> new BusinessException(ErrorCode.RESERVATION_NOT_FOUND));

        if (!reservation.getUser().getId().equals(user.getId())
                || !reservation.getStore().getId().equals(store.getId())
                || !"COMPLETED".equals(reservation.getStatus())) {
            throw new BusinessException(ErrorCode.REVIEW_NOT_ALLOWED);
        }

        if (reviewRepository.existsByReservation(reservation)) {
            throw new BusinessException(ErrorCode.REVIEW_ALREADY_EXISTS);
        }

        Review review = Review.builder()
                .user(user)
                .store(store)
                .reservation(reservation)
                .rating(request.getRating())
                .content(request.getContent().trim())
                .build();

        Review saved = reviewRepository.save(review);
        return new ReviewCreateResponse(
                saved.getId(),
                store.getId(),
                reservation.getId(),
                saved.getRating(),
                saved.getContent()
        );
    }

    @Transactional
    public ReviewUpdateResponse updateReview(String authorization, Long reviewId, ReviewUpdateRequest request) {
        if (request == null
                || request.getRating() == null
                || request.getContent() == null
                || request.getContent().isBlank()) {
            throw new BusinessException(ErrorCode.REQUIRED_FIELD_MISSING);
        }

        Long userId = authService.getAuthenticatedUserId(authorization);
        userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.AUTH_USER_NOT_FOUND));

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new BusinessException(ErrorCode.REVIEW_NOT_FOUND));

        if (!review.getUser().getId().equals(userId)) {
            throw new BusinessException(ErrorCode.REVIEW_ACCESS_DENIED);
        }

        review.update(request.getRating(), request.getContent().trim());
        return new ReviewUpdateResponse(review.getId(), review.getRating(), review.getContent());
    }

    @Transactional
    public void deleteReview(String authorization, Long reviewId) {
        Long userId = authService.getAuthenticatedUserId(authorization);
        userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.AUTH_USER_NOT_FOUND));

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new BusinessException(ErrorCode.REVIEW_NOT_FOUND));

        if (!review.getUser().getId().equals(userId)) {
            throw new BusinessException(ErrorCode.REVIEW_ACCESS_DENIED);
        }

        reviewRepository.delete(review);
    }

    @Transactional(readOnly = true)
    public StoreReviewListResponse getStoreReviews(Long storeId, int page, int size) {
        storeRepository.findById(storeId)
                .orElseThrow(() -> new BusinessException(ErrorCode.STORE_NOT_FOUND));

        int safePage = Math.max(0, page);
        int safeSize = Math.min(100, Math.max(1, size));

        long reviewCount = reviewRepository.countByStore_Id(storeId);
        Double avgRaw = reviewRepository.averageRatingByStoreId(storeId);
        BigDecimal averageRating = BigDecimal.valueOf(avgRaw == null ? 0.0 : avgRaw)
                .setScale(1, RoundingMode.HALF_UP);

        var pageable = PageRequest.of(safePage, safeSize);
        var result = reviewRepository.findByStore_IdOrderByCreatedAtDesc(storeId, pageable);

        var content = result.getContent().stream()
                .map(r -> new StoreReviewSummaryResponse(
                        r.getId(),
                        r.getUser().getName(),
                        r.getRating(),
                        r.getContent(),
                        r.getCreatedAt().toString()
                ))
                .toList();

        return new StoreReviewListResponse(
                averageRating,
                reviewCount,
                content,
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages()
        );
    }

    @Transactional(readOnly = true)
    public MyReviewListResponse getMyReviews(String authorization, int page, int size) {
        Long userId = authService.getAuthenticatedUserId(authorization);
        userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.AUTH_USER_NOT_FOUND));

        int safePage = Math.max(0, page);
        int safeSize = Math.min(100, Math.max(1, size));

        var pageable = PageRequest.of(safePage, safeSize);
        var result = reviewRepository.findByUser_IdOrderByCreatedAtDesc(userId, pageable);

        var content = result.getContent().stream()
                .map(r -> {
                    Store store = r.getStore();
                    return new MyReviewSummaryResponse(
                            r.getId(),
                            store.getId(),
                            store.getName(),
                            r.getRating(),
                            r.getContent(),
                            r.getCreatedAt().toString()
                    );
                })
                .toList();

        return new MyReviewListResponse(
                content,
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages()
        );
    }
}

