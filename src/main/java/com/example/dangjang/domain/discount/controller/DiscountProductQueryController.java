package com.example.dangjang.domain.discount.controller;

import com.example.dangjang.common.response.ApiResponse;
import com.example.dangjang.domain.discount.dto.DiscountProductListResponse;
import com.example.dangjang.domain.discount.service.DiscountProductQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/discount-products")
@RequiredArgsConstructor
public class DiscountProductQueryController {

    private final DiscountProductQueryService discountProductQueryService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<DiscountProductListResponse> getDiscountProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        DiscountProductListResponse response = discountProductQueryService.getDiscountProducts(page, size);
        return ApiResponse.ok("할인 상품 목록 조회 성공", response);
    }
}

