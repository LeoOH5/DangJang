package com.example.dangjang.domain.product.dto;

import com.example.dangjang.domain.product.entity.ProductStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
public class ProductCreateRequest {

    @NotNull(message = "storeId는 필수입니다.")
    private Long storeId;

    @NotBlank(message = "name은 필수입니다.")
    @Size(max = 150, message = "name은 최대 150자입니다.")
    private String name;

    @Size(max = 10_000, message = "description이 너무 깁니다.")
    private String description;

    @NotNull(message = "originalPrice는 필수입니다.")
    @Min(value = 0, message = "originalPrice는 0 이상이어야 합니다.")
    private BigDecimal originalPrice;

    @NotNull(message = "stockQuantity는 필수입니다.")
    @Min(value = 0, message = "stockQuantity는 0 이상이어야 합니다.")
    private Integer stockQuantity;

    @Size(max = 500, message = "imageUrl은 최대 500자입니다.")
    private String imageUrl;

    @NotNull(message = "status는 필수입니다.")
    private ProductStatus status;
}
