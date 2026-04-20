package com.example.dangjang.domain.product.controller;

import com.example.dangjang.common.annotation.Auth;
import com.example.dangjang.common.response.ApiResponse;
import com.example.dangjang.domain.product.dto.ProductCreateRequest;
import com.example.dangjang.domain.product.dto.ProductCreateResponse;
import com.example.dangjang.domain.product.dto.ProductStatusUpdateRequest;
import com.example.dangjang.domain.product.dto.ProductStatusUpdateResponse;
import com.example.dangjang.domain.product.dto.ProductUpdateRequest;
import com.example.dangjang.domain.product.dto.ProductUpdateResponse;
import com.example.dangjang.domain.product.service.ProductService;
import com.example.dangjang.domain.user.entity.Role;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/products")
@RequiredArgsConstructor
@Auth(roles = Role.ADMIN)
public class AdminProductController {

    private final ProductService productService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<ProductCreateResponse> createProduct(
            @Valid @RequestBody ProductCreateRequest request
    ) {
        ProductCreateResponse response = productService.createProduct(request);
        return ApiResponse.created("상품 등록 성공", response);
    }

    @PutMapping("/{productId}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<ProductUpdateResponse> updateProduct(
            @PathVariable Long productId,
            @Valid @RequestBody ProductUpdateRequest request
    ) {
        ProductUpdateResponse response = productService.updateProduct(productId, request);
        return ApiResponse.ok("상품 수정 성공", response);
    }

    @PatchMapping("/{productId}/status")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<ProductStatusUpdateResponse> updateProductStatus(
            @PathVariable Long productId,
            @Valid @RequestBody ProductStatusUpdateRequest request
    ) {
        ProductStatusUpdateResponse response = productService.updateProductStatus(productId, request);
        return ApiResponse.ok("상품 상태 변경 성공", response);
    }
}
