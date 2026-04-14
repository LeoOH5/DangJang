package com.example.dangjang.domain.product.dto;

import com.example.dangjang.domain.product.entity.ProductStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class ProductDetailResponse {

    private final Long productId;
    private final String productName;
    private final String description;
    private final Long storeId;
    private final String storeName;
    private final Long marketId;
    private final String marketName;
    private final BigDecimal originalPrice;
    private final Integer stockQuantity;
    private final String imageUrl;
    private final ProductStatus status;
    private final boolean hasActiveDiscount;
    private final BigDecimal discountPrice;
    private final String discountLabel;
}
