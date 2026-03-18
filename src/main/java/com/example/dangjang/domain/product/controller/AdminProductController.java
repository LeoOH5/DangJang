package com.example.dangjang.domain.product.controller;

import com.example.dangjang.common.annotation.AdminOnly;
import com.example.dangjang.common.response.ApiResponse;
import com.example.dangjang.domain.product.dto.ProductCreateRequest;
import com.example.dangjang.domain.product.dto.ProductCreateResponse;
import com.example.dangjang.domain.product.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/products")
@RequiredArgsConstructor
public class AdminProductController {

    private final ProductService productService;

    @AdminOnly
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<ProductCreateResponse> createProduct(
            @Valid @RequestBody ProductCreateRequest request
    ) {
        ProductCreateResponse response = productService.createProduct(request);
        return ApiResponse.created("상품 등록 성공", response);
    }
}
