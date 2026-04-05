package com.example.dangjang.domain.store.controller;

import com.example.dangjang.common.response.ApiResponse;
import com.example.dangjang.domain.store.dto.StoreDetailResponse;
import com.example.dangjang.domain.store.dto.StoreSearchResponse;
import com.example.dangjang.domain.store.service.StoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/stores")
@RequiredArgsConstructor
public class StoreQueryController {

    private final StoreService storeService;

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<StoreSearchResponse> searchStores(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long marketId,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String district,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        StoreSearchResponse response =
                storeService.searchStores(keyword, marketId, city, district, page, size);
        return ApiResponse.ok("매장 검색 성공", response);
    }

    @GetMapping("/{storeId}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<StoreDetailResponse> getStoreDetail(@PathVariable Long storeId) {
        StoreDetailResponse response = storeService.getStoreDetail(storeId);
        return ApiResponse.ok("매장 상세 조회 성공", response);
    }
}

