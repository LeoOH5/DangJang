package com.example.dangjang.domain.market.controller;

import com.example.dangjang.common.response.ApiResponse;
import com.example.dangjang.domain.market.dto.MarketDetailResponse;
import com.example.dangjang.domain.market.dto.MarketListResponse;
import com.example.dangjang.domain.market.service.MarketService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/markets")
@RequiredArgsConstructor
public class MarketQueryController {

    private final MarketService marketService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<MarketListResponse> getMarkets(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        MarketListResponse response = marketService.getMarkets(page, size);
        return ApiResponse.ok("시장 목록 조회 성공", response);
    }

    @GetMapping("/{marketId}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<MarketDetailResponse> getMarketDetail(@PathVariable Long marketId) {
        MarketDetailResponse response = marketService.getMarketDetail(marketId);
        return ApiResponse.ok("시장 상세 조회 성공", response);
    }
}

