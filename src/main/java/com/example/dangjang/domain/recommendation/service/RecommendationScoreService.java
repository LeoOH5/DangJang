package com.example.dangjang.domain.recommendation.service;

import com.example.dangjang.common.exception.BusinessException;
import com.example.dangjang.common.exception.ErrorCode;
import com.example.dangjang.domain.discount.entity.DiscountStatus;
import com.example.dangjang.domain.discount.entity.ProductDiscount;
import com.example.dangjang.domain.discount.repository.ProductDiscountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RecommendationScoreService {

    private static final ZoneId SEOUL = ZoneId.of("Asia/Seoul");
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE;

    private static final double SEARCH_WEIGHT = 0.3;
    private static final double VIEW_WEIGHT = 0.2;
    private static final double RESERVATION_WEIGHT = 0.5;

    private final StringRedisTemplate stringRedisTemplate;
    private final ProductDiscountRepository productDiscountRepository;

    public void recordSearchEvent(List<Long> productIds) {
        if (productIds == null || productIds.isEmpty()) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
        }

        boolean recorded = false;
        for (Long productId : productIds) {
            if (productId == null) {
                continue;
            }
            Long discountId = resolveActiveDiscountId(productId);
            if (discountId == null) {
                continue;
            }
            incrementScore(discountId, SEARCH_WEIGHT);
            recorded = true;
        }

        if (!recorded) {
            throw new BusinessException(ErrorCode.RECOMMENDATION_PRODUCT_NOT_FOUND);
        }
    }

    public void recordViewEvent(Long productId) {
        if (productId == null) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
        }
        Long discountId = resolveActiveDiscountId(productId);
        if (discountId == null) {
            throw new BusinessException(ErrorCode.RECOMMENDATION_PRODUCT_NOT_FOUND);
        }
        incrementScore(discountId, VIEW_WEIGHT);
    }

    public void trackReservation(Long productDiscountId, int quantity) {
        if (productDiscountId == null || quantity <= 0) {
            return;
        }
        incrementScore(productDiscountId, RESERVATION_WEIGHT * quantity);
    }

    private Long resolveActiveDiscountId(Long productId) {
        List<ProductDiscount> candidates = productDiscountRepository.findActiveCandidatesByProductId(
                productId,
                DiscountStatus.ACTIVE,
                LocalDateTime.now(SEOUL)
        );
        if (candidates.isEmpty()) {
            return null;
        }
        return candidates.get(0).getId();
    }

    private void incrementScore(Long productDiscountId, double delta) {
        String key = scoreKey(LocalDate.now(SEOUL));
        String member = productDiscountId.toString();
        try {
            stringRedisTemplate.opsForZSet().incrementScore(key, member, delta);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.RECOMMENDATION_AGGREGATION_FAILED);
        }
    }

    public String scoreKey(LocalDate date) {
        return "rec:popular-discount:" + date.format(DATE_FORMAT);
    }
}
