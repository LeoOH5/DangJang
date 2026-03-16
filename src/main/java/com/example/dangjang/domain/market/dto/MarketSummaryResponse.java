package com.example.dangjang.domain.market.dto;

import com.example.dangjang.domain.market.entity.MarketStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MarketSummaryResponse {

    private final Long marketId;
    private final String name;
    private final String description;
    private final String address;
    private final String city;
    private final String district;
    private final MarketStatus status;
}

