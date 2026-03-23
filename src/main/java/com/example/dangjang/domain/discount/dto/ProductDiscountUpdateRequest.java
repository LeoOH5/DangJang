package com.example.dangjang.domain.discount.dto;

import com.example.dangjang.domain.discount.entity.DiscountStatus;
import com.example.dangjang.domain.discount.entity.DiscountType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
public class ProductDiscountUpdateRequest {

    @NotBlank(message = "title은 필수입니다.")
    private String title;

    @NotNull(message = "discountType은 필수입니다.")
    private DiscountType discountType;

    @NotNull(message = "discountValue는 필수입니다.")
    private BigDecimal discountValue;

    @NotNull(message = "startAt은 필수입니다.")
    private String startAt;

    @NotNull(message = "endAt은 필수입니다.")
    private String endAt;

    @NotNull(message = "remainingQuantity는 필수입니다.")
    private Integer remainingQuantity;

    @NotNull(message = "status는 필수입니다.")
    private DiscountStatus status;
}

