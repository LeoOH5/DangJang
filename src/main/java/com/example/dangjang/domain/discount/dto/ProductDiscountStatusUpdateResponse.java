package com.example.dangjang.domain.discount.dto;

import com.example.dangjang.domain.discount.entity.DiscountStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProductDiscountStatusUpdateResponse {

    private final Long productDiscountId;
    private final DiscountStatus status;
}

