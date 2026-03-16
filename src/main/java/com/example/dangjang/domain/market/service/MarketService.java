package com.example.dangjang.domain.market.service;

import com.example.dangjang.common.exception.BusinessException;
import com.example.dangjang.common.exception.ErrorCode;
import com.example.dangjang.domain.market.dto.MarketCreateRequest;
import com.example.dangjang.domain.market.dto.MarketCreateResponse;
import com.example.dangjang.domain.market.dto.MarketDetailResponse;
import com.example.dangjang.domain.market.dto.MarketListResponse;
import com.example.dangjang.domain.market.dto.MarketSummaryResponse;
import com.example.dangjang.domain.market.dto.MarketUpdateRequest;
import com.example.dangjang.domain.market.dto.MarketUpdateResponse;
import com.example.dangjang.domain.market.dto.StoreSummaryResponse;
import com.example.dangjang.domain.market.entity.Market;
import com.example.dangjang.domain.market.repository.MarketRepository;
import com.example.dangjang.domain.store.entity.Store;
import com.example.dangjang.domain.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MarketService {

    private final MarketRepository marketRepository;
    private final StoreRepository storeRepository;

    @Transactional
    public MarketCreateResponse createMarket(MarketCreateRequest request) {
        boolean exists = marketRepository.existsByNameAndAddress(request.getName(), request.getAddress());
        if (exists) {
            throw new BusinessException(ErrorCode.MARKET_ALREADY_EXISTS);
        }

        Market market = Market.builder()
                .name(request.getName())
                .description(request.getDescription())
                .address(request.getAddress())
                .city(request.getCity())
                .district(request.getDistrict())
                .status(request.getStatus())
                .build();

        Market saved = marketRepository.save(market);
        return new MarketCreateResponse(saved.getId());
    }

    @Transactional
    public MarketUpdateResponse updateMarket(Long marketId, MarketUpdateRequest request) {
        Market market = marketRepository.findById(marketId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MARKET_NOT_FOUND));

        marketRepository.findByNameAndAddress(request.getName(), request.getAddress())
                .filter(found -> !found.getId().equals(marketId))
                .ifPresent(found -> {
                    throw new BusinessException(ErrorCode.MARKET_ALREADY_EXISTS);
                });

        market.update(
                request.getName(),
                request.getDescription(),
                request.getAddress(),
                request.getCity(),
                request.getDistrict(),
                request.getStatus()
        );

        return new MarketUpdateResponse(market.getId());
    }

    @Transactional(readOnly = true)
    public MarketListResponse getMarkets(int page, int size) {
        PageRequest pageable = PageRequest.of(page, size);
        Page<Market> markets = marketRepository.findAll(pageable);

        List<MarketSummaryResponse> content = markets.getContent().stream()
                .map(market -> new MarketSummaryResponse(
                        market.getId(),
                        market.getName(),
                        market.getDescription(),
                        market.getAddress(),
                        market.getCity(),
                        market.getDistrict(),
                        market.getStatus()
                ))
                .toList();

        return new MarketListResponse(
                content,
                markets.getNumber(),
                markets.getSize(),
                markets.getTotalElements(),
                markets.getTotalPages()
        );
    }

    @Transactional(readOnly = true)
    public MarketDetailResponse getMarketDetail(Long marketId) {
        Market market = marketRepository.findById(marketId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MARKET_NOT_FOUND));

        List<Store> stores = storeRepository.findByMarket(market);

        List<StoreSummaryResponse> storeResponses = stores.stream()
                .map(store -> new StoreSummaryResponse(
                        store.getId(),
                        store.getName(),
                        store.getDescription(),
                        store.getPhone(),
                        store.getStatus()
                ))
                .toList();

        return new MarketDetailResponse(
                market.getId(),
                market.getName(),
                market.getDescription(),
                market.getAddress(),
                market.getCity(),
                market.getDistrict(),
                market.getStatus(),
                storeResponses
        );
    }
}

