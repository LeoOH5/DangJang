package com.example.dangjang.domain.store.service;

import com.example.dangjang.common.exception.BusinessException;
import com.example.dangjang.common.exception.ErrorCode;
import com.example.dangjang.domain.discount.entity.DiscountStatus;
import com.example.dangjang.domain.discount.entity.ProductDiscount;
import com.example.dangjang.domain.discount.repository.ProductDiscountRepository;
import com.example.dangjang.domain.market.entity.Market;
import com.example.dangjang.domain.market.entity.MarketStatus;
import com.example.dangjang.domain.market.repository.MarketRepository;
import com.example.dangjang.domain.product.entity.Product;
import com.example.dangjang.domain.product.repository.ProductRepository;
import com.example.dangjang.domain.store.dto.ProductWithDiscountResponse;
import com.example.dangjang.domain.store.dto.StoreCreateRequest;
import com.example.dangjang.domain.store.dto.StoreCreateResponse;
import com.example.dangjang.domain.store.dto.StoreDetailResponse;
import com.example.dangjang.domain.store.dto.StoreSearchResponse;
import com.example.dangjang.domain.store.dto.StoreSearchSummaryResponse;
import com.example.dangjang.domain.store.dto.StoreUpdateRequest;
import com.example.dangjang.domain.store.dto.StoreUpdateResponse;
import com.example.dangjang.domain.store.entity.Store;
import com.example.dangjang.domain.store.entity.StoreStatus;
import com.example.dangjang.domain.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StoreService {

    private final StoreRepository storeRepository;
    private final MarketRepository marketRepository;
    private final ProductRepository productRepository;
    private final ProductDiscountRepository productDiscountRepository;

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
                .owner(null)
                .build();

        Store saved = storeRepository.save(store);
        return new StoreCreateResponse(saved.getId());
    }

    @Transactional
    public StoreUpdateResponse updateStore(Long storeId, StoreUpdateRequest request) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new BusinessException(ErrorCode.STORE_NOT_FOUND));

        LocalTime openTime = LocalTime.parse(request.getOpenTime());
        LocalTime closeTime = LocalTime.parse(request.getCloseTime());

        if (openTime.isAfter(closeTime)) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
        }

        store.update(
                request.getName(),
                request.getDescription(),
                request.getPhone(),
                request.getAddress(),
                openTime,
                closeTime,
                request.getStatus()
        );

        return new StoreUpdateResponse(store.getId());
    }

    @Transactional(readOnly = true)
    public StoreSearchResponse searchStores(
            String keyword,
            Long marketId,
            String city,
            String district,
            int page,
            int size
    ) {
        String kw = keyword != null ? keyword.trim() : "";
        String c = city != null ? city.trim() : "";
        String d = district != null ? district.trim() : "";

        boolean hasKeyword = !kw.isEmpty();
        boolean hasMarket = marketId != null;
        boolean hasCity = !c.isEmpty();
        boolean hasDistrict = !d.isEmpty();
        if (!hasKeyword && !hasMarket && !hasCity && !hasDistrict) {
            throw new BusinessException(ErrorCode.INVALID_SEARCH_CONDITION);
        }

        var pageable = PageRequest.of(page, size);
        var result = storeRepository.searchStores(
                hasKeyword ? kw : null,
                marketId,
                hasCity ? c : null,
                hasDistrict ? d : null,
                StoreStatus.INACTIVE,
                MarketStatus.ACTIVE,
                pageable
        );

        var content = result.getContent().stream()
                .map(store -> {
                    Market m = store.getMarket();
                    return new StoreSearchSummaryResponse(
                            store.getId(),
                            m.getId(),
                            m.getName(),
                            m.getCity(),
                            m.getDistrict(),
                            store.getName(),
                            store.getDescription(),
                            store.getAddress(),
                            store.getStatus()
                    );
                })
                .toList();

        return new StoreSearchResponse(
                content,
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages()
        );
    }

    @Transactional(readOnly = true)
    public StoreDetailResponse getStoreDetail(Long storeId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new BusinessException(ErrorCode.STORE_NOT_FOUND));

        List<Product> products = productRepository.findByStore(store);
        LocalDateTime now = LocalDateTime.now();

        List<ProductWithDiscountResponse> productResponses = products.stream()
                .map(product -> {
                    List<ProductDiscount> discounts =
                            productDiscountRepository.findByProductAndStatus(product, DiscountStatus.ACTIVE);

                    ProductDiscount active = discounts.stream()
                            .filter(d -> d.isActiveNow(now))
                            .findFirst()
                            .orElse(null);

                    ProductWithDiscountResponse.ActiveDiscountResponse activeResponse = null;
                    boolean hasActiveDiscount = active != null;

                    if (active != null) {
                        activeResponse = new ProductWithDiscountResponse.ActiveDiscountResponse(
                                active.getId(),
                                active.getTitle(),
                                active.getDiscountType(),
                                active.getDiscountValue(),
                                active.getDiscountPrice(),
                                active.getStartAt(),
                                active.getEndAt(),
                                active.getStatus()
                        );
                    }

                    return new ProductWithDiscountResponse(
                            product.getId(),
                            product.getName(),
                            product.getDescription(),
                            product.getOriginalPrice(),
                            product.getStockQuantity(),
                            product.getImageUrl(),
                            product.getStatus(),
                            hasActiveDiscount,
                            activeResponse
                    );
                })
                .toList();

        return new StoreDetailResponse(
                store.getId(),
                store.getMarket().getId(),
                store.getName(),
                store.getDescription(),
                store.getPhone(),
                store.getAddress(),
                store.getOpenTime() != null ? store.getOpenTime().toString() : null,
                store.getCloseTime() != null ? store.getCloseTime().toString() : null,
                store.getStatus(),
                productResponses
        );
    }
}

