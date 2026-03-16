package com.example.dangjang.domain.store.controller;

import com.example.dangjang.common.annotation.AdminOnly;
import com.example.dangjang.common.response.ApiResponse;
import com.example.dangjang.domain.store.dto.StoreCreateRequest;
import com.example.dangjang.domain.store.dto.StoreCreateResponse;
import com.example.dangjang.domain.store.dto.StoreUpdateRequest;
import com.example.dangjang.domain.store.dto.StoreUpdateResponse;
import com.example.dangjang.domain.store.service.StoreService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/stores")
@RequiredArgsConstructor
public class AdminStoreController {

    private final StoreService storeService;

    @AdminOnly
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<StoreCreateResponse> createStore(
            @Valid @RequestBody StoreCreateRequest request
    ) {
        StoreCreateResponse response = storeService.createStore(request);
        return ApiResponse.created("매장 등록 성공", response);
    }

    @AdminOnly
    @PutMapping("/{storeId}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<StoreUpdateResponse> updateStore(
            @PathVariable Long storeId,
            @Valid @RequestBody StoreUpdateRequest request
    ) {
        StoreUpdateResponse response = storeService.updateStore(storeId, request);
        return ApiResponse.ok("매장 수정 성공", response);
    }
}

