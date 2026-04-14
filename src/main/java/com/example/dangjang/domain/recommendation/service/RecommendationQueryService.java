package com.example.dangjang.domain.recommendation.service;

import com.example.dangjang.common.exception.BusinessException;
import com.example.dangjang.common.exception.ErrorCode;
import com.example.dangjang.domain.discount.entity.ProductDiscount;
import com.example.dangjang.domain.discount.repository.ProductDiscountRepository;
import com.example.dangjang.domain.recommendation.dto.PopularDiscountRecommendationItemResponse;
import com.example.dangjang.domain.recommendation.dto.PopularDiscountRecommendationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RecommendationQueryService {

    private static final ZoneId SEOUL = ZoneId.of("Asia/Seoul");

    private final StringRedisTemplate stringRedisTemplate;
    private final RecommendationScoreService recommendationScoreService;
    private final ProductDiscountRepository productDiscountRepository;

    @Transactional(readOnly = true)
    public PopularDiscountRecommendationResponse getPopularDiscountProducts(int size) {
        if (size <= 0) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
        }

        LocalDate today = LocalDate.now(SEOUL);
        String key = recommendationScoreService.scoreKey(today);

        Set<ZSetOperations.TypedTuple<String>> tuples;
        try {
            tuples = stringRedisTemplate.opsForZSet().reverseRangeWithScores(key, 0, size - 1L);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.RECOMMENDATION_AGGREGATION_FAILED);
        }

        if (tuples == null || tuples.isEmpty()) {
            throw new BusinessException(ErrorCode.RECOMMENDATION_DATA_NOT_READY);
        }

        List<Long> discountIds = new ArrayList<>();
        Map<Long, Double> scoreByDiscountId = new LinkedHashMap<>();
        for (ZSetOperations.TypedTuple<String> tuple : tuples) {
            if (tuple == null || tuple.getValue() == null) {
                continue;
            }
            Long discountId;
            try {
                discountId = Long.parseLong(tuple.getValue());
            } catch (NumberFormatException ignored) {
                continue;
            }
            discountIds.add(discountId);
            scoreByDiscountId.put(discountId, tuple.getScore() == null ? 0.0 : tuple.getScore());
        }

        if (discountIds.isEmpty()) {
            throw new BusinessException(ErrorCode.RECOMMENDATION_PRODUCT_NOT_FOUND);
        }

        List<ProductDiscount> discounts = productDiscountRepository.findAllByIdInWithProductStoreMarket(discountIds);
        if (discounts.isEmpty()) {
            throw new BusinessException(ErrorCode.RECOMMENDATION_PRODUCT_NOT_FOUND);
        }

        Map<Long, ProductDiscount> discountMap = new LinkedHashMap<>();
        discounts.forEach(d -> discountMap.put(d.getId(), d));

        List<PopularDiscountRecommendationItemResponse> content = new ArrayList<>();
        int rank = 1;
        for (Long discountId : discountIds) {
            ProductDiscount pd = discountMap.get(discountId);
            if (pd == null) {
                continue;
            }
            var product = pd.getProduct();
            var store = product.getStore();
            var market = store.getMarket();

            content.add(new PopularDiscountRecommendationItemResponse(
                    rank++,
                    pd.getId(),
                    product.getId(),
                    product.getName(),
                    store.getId(),
                    store.getName(),
                    market.getId(),
                    market.getName(),
                    product.getOriginalPrice(),
                    pd.getDiscountPrice(),
                    pd.getRemainingQuantity(),
                    pd.getStatus().name(),
                    roundOneDecimal(scoreByDiscountId.getOrDefault(discountId, 0.0))
            ));
        }

        if (content.isEmpty()) {
            throw new BusinessException(ErrorCode.RECOMMENDATION_PRODUCT_NOT_FOUND);
        }

        content.sort(Comparator.comparingInt(PopularDiscountRecommendationItemResponse::getRank));
        return new PopularDiscountRecommendationResponse(today.toString(), content);
    }

    private double roundOneDecimal(double value) {
        return Math.round(value * 10.0) / 10.0;
    }
}
