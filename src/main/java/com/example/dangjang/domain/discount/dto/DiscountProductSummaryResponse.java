package com.example.dangjang.domain.discount.dto;

import com.example.dangjang.domain.discount.entity.DiscountStatus;
import com.example.dangjang.domain.discount.entity.DiscountType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class DiscountProductSummaryResponse {

    private final Long productId;
    private final Long productDiscountId;
    private final String productName;
    private final Long storeId;
    private final String storeName;
    private final Long marketId;
    private final String marketName;
    private final BigDecimal originalPrice;
    private final BigDecimal discountPrice;
    private final DiscountType discountType;
    private final BigDecimal discountValue;
    private final LocalDateTime startAt;
    private final LocalDateTime endAt;
    private final Integer remainingQuantity;
    private final DiscountStatus status;
}

