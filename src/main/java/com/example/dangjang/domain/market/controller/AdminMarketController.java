package com.example.dangjang.domain.market.controller;

import com.example.dangjang.common.annotation.AdminOnly;
import com.example.dangjang.common.response.ApiResponse;
import com.example.dangjang.domain.market.dto.MarketCreateRequest;
import com.example.dangjang.domain.market.dto.MarketCreateResponse;
import com.example.dangjang.domain.market.dto.MarketUpdateRequest;
import com.example.dangjang.domain.market.dto.MarketUpdateResponse;
import com.example.dangjang.domain.market.service.MarketService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/markets")
@RequiredArgsConstructor
public class AdminMarketController {

    private final MarketService marketService;

    @AdminOnly
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<MarketCreateResponse> createMarket(@Valid @RequestBody MarketCreateRequest request) {
        MarketCreateResponse response = marketService.createMarket(request);
        return ApiResponse.created("시장 등록 성공", response);
    }

    @AdminOnly
    @PutMapping("/{marketId}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<MarketUpdateResponse> updateMarket(
            @PathVariable Long marketId,
            @Valid @RequestBody MarketUpdateRequest request
    ) {
        MarketUpdateResponse response = marketService.updateMarket(marketId, request);
        return ApiResponse.ok("시장 수정 성공", response);
    }
}

