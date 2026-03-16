package com.example.dangjang.domain.store.dto;

import com.example.dangjang.domain.store.entity.StoreStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class StoreDetailResponse {

    private final Long storeId;
    private final Long marketId;
    private final String name;
    private final String description;
    private final String phone;
    private final String address;
    private final String openTime;
    private final String closeTime;
    private final StoreStatus status;
    private final List<ProductWithDiscountResponse> products;
}

