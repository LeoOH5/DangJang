package com.example.dangjang.domain.store.dto;

import com.example.dangjang.domain.discount.entity.DiscountStatus;
import com.example.dangjang.domain.discount.entity.DiscountType;
import com.example.dangjang.domain.product.entity.ProductStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ProductWithDiscountResponse {

    private final Long productId;
    private final String name;
    private final String description;
    private final BigDecimal originalPrice;
    private final Integer stockQuantity;
    private final String imageUrl;
    private final ProductStatus status;
    private final boolean hasActiveDiscount;
    private final ActiveDiscountResponse activeDiscount;

    @Getter
    @AllArgsConstructor
    public static class ActiveDiscountResponse {
        private final Long productDiscountId;
        private final String title;
        private final DiscountType discountType;
        private final BigDecimal discountValue;
        private final BigDecimal discountPrice;
        private final LocalDateTime startAt;
        private final LocalDateTime endAt;
        private final DiscountStatus status;
    }
}

