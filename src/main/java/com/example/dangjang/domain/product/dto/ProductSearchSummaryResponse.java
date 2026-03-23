package com.example.dangjang.domain.product.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class ProductSearchSummaryResponse {

    private final Long productId;
    private final Long storeId;
    private final String storeName;
    private final Long marketId;
    private final String marketName;
    private final String name;
    private final BigDecimal originalPrice;
    private final String imageUrl;
    private final boolean hasActiveDiscount;
    private final BigDecimal discountPrice;
    private final String discountLabel;
}

