package com.example.dangjang.domain.discount.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class DiscountProductListResponse {

    private final List<DiscountProductSummaryResponse> content;
    private final int page;
    private final int size;
    private final long totalElements;
    private final int totalPages;
}

