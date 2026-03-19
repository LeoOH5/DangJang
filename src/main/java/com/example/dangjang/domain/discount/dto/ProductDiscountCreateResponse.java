package com.example.dangjang.domain.discount.dto;

import com.example.dangjang.domain.discount.entity.DiscountStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class ProductDiscountCreateResponse {

    private final Long productDiscountId;
    private final BigDecimal discountPrice;
    private final DiscountStatus status;
}

