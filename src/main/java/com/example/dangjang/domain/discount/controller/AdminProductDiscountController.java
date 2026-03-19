package com.example.dangjang.domain.discount.controller;

import com.example.dangjang.common.annotation.AdminOnly;
import com.example.dangjang.common.response.ApiResponse;
import com.example.dangjang.domain.discount.dto.ProductDiscountCreateRequest;
import com.example.dangjang.domain.discount.dto.ProductDiscountCreateResponse;
import com.example.dangjang.domain.discount.service.ProductDiscountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/product-discounts")
@RequiredArgsConstructor
public class AdminProductDiscountController {

    private final ProductDiscountService productDiscountService;

    @AdminOnly
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<ProductDiscountCreateResponse> createProductDiscount(
            @Valid @RequestBody ProductDiscountCreateRequest request
    ) {
        ProductDiscountCreateResponse response = productDiscountService.createProductDiscount(request);
        return ApiResponse.created("할인 등록 성공", response);
    }
}

