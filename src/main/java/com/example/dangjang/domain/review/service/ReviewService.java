package com.example.dangjang.domain.review.service;

import com.example.dangjang.common.exception.BusinessException;
import com.example.dangjang.common.exception.ErrorCode;
import com.example.dangjang.domain.auth.service.AuthService;
import com.example.dangjang.domain.reservation.entity.Reservation;
import com.example.dangjang.domain.reservation.repository.ReservationRepository;
import com.example.dangjang.domain.review.dto.ReviewCreateRequest;
import com.example.dangjang.domain.review.dto.ReviewCreateResponse;
import com.example.dangjang.domain.review.dto.ReviewUpdateRequest;
import com.example.dangjang.domain.review.dto.ReviewUpdateResponse;
import com.example.dangjang.domain.review.entity.Review;
import com.example.dangjang.domain.review.repository.ReviewRepository;
import com.example.dangjang.domain.store.entity.Store;
import com.example.dangjang.domain.store.repository.StoreRepository;
import com.example.dangjang.domain.user.entity.User;
import com.example.dangjang.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}

