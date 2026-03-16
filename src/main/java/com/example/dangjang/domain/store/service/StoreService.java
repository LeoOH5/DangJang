package com.example.dangjang.domain.store.service;

import com.example.dangjang.common.exception.BusinessException;
import com.example.dangjang.common.exception.ErrorCode;
import com.example.dangjang.domain.market.entity.Market;
import com.example.dangjang.domain.market.entity.MarketStatus;
import com.example.dangjang.domain.market.repository.MarketRepository;
import com.example.dangjang.domain.store.dto.StoreCreateRequest;
import com.example.dangjang.domain.store.dto.StoreCreateResponse;
import com.example.dangjang.domain.store.entity.Store;
import com.example.dangjang.domain.store.entity.StoreStatus;
import com.example.dangjang.domain.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;

@Service
@RequiredArgsConstructor
public class StoreService {

    private final StoreRepository storeRepository;
    private final MarketRepository marketRepository;

    @Transactional
    public StoreCreateResponse createStore(StoreCreateRequest request) {
        Market market = marketRepository.findById(request.getMarketId())
                .orElseThrow(() -> new BusinessException(ErrorCode.MARKET_NOT_FOUND));

        if (market.getStatus() == MarketStatus.INACTIVE) {
            throw new BusinessException(ErrorCode.MARKET_INACTIVE);
        }

        LocalTime openTime = LocalTime.parse(request.getOpenTime());
        LocalTime closeTime = LocalTime.parse(request.getCloseTime());

        if (openTime.isAfter(closeTime)) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
        }

        StoreStatus status = request.getStatus();

        Store store = Store.builder()
                .market(market)
                .name(request.getName())
                .description(request.getDescription())
                .phone(request.getPhone())
                .address(request.getAddress())
                .openTime(openTime)
                .closeTime(closeTime)
                .status(status)
                .build();

        Store saved = storeRepository.save(store);
        return new StoreCreateResponse(saved.getId());
    }
}

