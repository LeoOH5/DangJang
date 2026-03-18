package com.example.dangjang.domain.product.dto;

import com.example.dangjang.domain.product.entity.ProductStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProductStatusUpdateRequest {

    @NotNull(message = "status는 필수입니다.")
    private ProductStatus status;
}

