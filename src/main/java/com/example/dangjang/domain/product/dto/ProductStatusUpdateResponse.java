package com.example.dangjang.domain.product.dto;

import com.example.dangjang.domain.product.entity.ProductStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProductStatusUpdateResponse {

    private final Long productId;
    private final ProductStatus status;
}

