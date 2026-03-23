package com.example.dangjang.domain.discount.dto;

import com.example.dangjang.domain.discount.entity.DiscountStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProductDiscountStatusUpdateRequest {

    @NotNull(message = "status는 필수입니다.")
    private DiscountStatus status;
}

