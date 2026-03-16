package com.example.dangjang.domain.market.dto;

import com.example.dangjang.domain.store.entity.StoreStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class StoreSummaryResponse {

    private final Long storeId;
    private final String name;
    private final String description;
    private final String phone;
    private final StoreStatus status;
}

