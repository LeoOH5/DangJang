package com.example.dangjang.domain.store.dto;

import com.example.dangjang.domain.store.entity.StoreStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class StoreSearchSummaryResponse {

    private final Long storeId;
    private final Long marketId;
    private final String marketName;
    private final String city;
    private final String district;
    private final String name;
    private final String description;
    private final String address;
    private final StoreStatus status;
}
