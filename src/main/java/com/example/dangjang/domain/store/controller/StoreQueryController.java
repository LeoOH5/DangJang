package com.example.dangjang.domain.store.controller;

import com.example.dangjang.common.response.ApiResponse;
import com.example.dangjang.domain.store.dto.StoreDetailResponse;
import com.example.dangjang.domain.store.service.StoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/stores")
@RequiredArgsConstructor
public class StoreQueryController {

    private final StoreService storeService;

    @GetMapping("/{storeId}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<StoreDetailResponse> getStoreDetail(@PathVariable Long storeId) {
        StoreDetailResponse response = storeService.getStoreDetail(storeId);
        return ApiResponse.ok("매장 상세 조회 성공", response);
    }
}

