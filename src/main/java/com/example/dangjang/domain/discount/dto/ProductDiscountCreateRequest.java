package com.example.dangjang.domain.discount.dto;

import com.example.dangjang.domain.discount.entity.DiscountType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
public class ProductDiscountCreateRequest {

    @NotNull(message = "productId는 필수입니다.")
    private Long productId;

    @NotBlank(message = "title은 필수입니다.")
    @Size(max = 150, message = "title은 최대 150자입니다.")
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
}

