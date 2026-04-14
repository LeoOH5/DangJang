package com.example.dangjang.domain.product.controller;

import com.example.dangjang.common.response.ApiResponse;
import com.example.dangjang.domain.product.dto.ProductDetailResponse;
import com.example.dangjang.domain.product.dto.ProductSearchResponse;
import com.example.dangjang.domain.product.service.ProductQueryService;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@Validated
@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductQueryController {

    private final ProductQueryService productQueryService;


    @GetMapping("/{productId}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<ProductDetailResponse> getProductDetail(@PathVariable Long productId) {
        ProductDetailResponse response = productQueryService.getProductDetail(productId);
        return ApiResponse.ok("상품 상세 조회 성공", response);
    }

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<ProductSearchResponse> searchProducts(
            @RequestParam @NotBlank(message = "keyword는 필수입니다.") String keyword,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        ProductSearchResponse response =
                productQueryService.searchProducts(keyword, minPrice, maxPrice, page, size);
        return ApiResponse.ok("상품 검색 성공", response);
    }
}

