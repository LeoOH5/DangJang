package com.example.dangjang.domain.discount.service;

import com.example.dangjang.domain.discount.dto.DiscountProductListResponse;
import com.example.dangjang.domain.discount.dto.DiscountProductSummaryResponse;
import com.example.dangjang.domain.discount.entity.DiscountStatus;
import com.example.dangjang.domain.discount.entity.ProductDiscount;
import com.example.dangjang.domain.discount.repository.ProductDiscountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class DiscountProductQueryService {

    private final ProductDiscountRepository productDiscountRepository;

    @Transactional(readOnly = true)
    public DiscountProductListResponse getDiscountProducts(int page, int size) {
        PageRequest pageable = PageRequest.of(page, size);
        LocalDateTime now = LocalDateTime.now();

        var discountPage =
                productDiscountRepository.findActiveDiscountProducts(
                        now,
                        DiscountStatus.ACTIVE,
                        DiscountStatus.SCHEDULED,
                        pageable
                );

        return new DiscountProductListResponse(
                discountPage.getContent().stream()
                        .map(this::toSummary)
                        .toList(),
                discountPage.getNumber(),
                discountPage.getSize(),
                discountPage.getTotalElements(),
                discountPage.getTotalPages()
        );
    }

    private DiscountProductSummaryResponse toSummary(ProductDiscount pd) {
        var product = pd.getProduct();
        var store = product.getStore();
        var market = store.getMarket();

        return new DiscountProductSummaryResponse(
                product.getId(),
                pd.getId(),
                product.getName(),
                store.getId(),
                store.getName(),
                market.getId(),
                market.getName(),
                product.getOriginalPrice(),
                pd.getDiscountPrice(),
                pd.getDiscountType().normalize(),
                pd.getDiscountValue(),
                pd.getStartAt(),
                pd.getEndAt(),
                pd.getRemainingQuantity(),
                pd.getStatus()
        );
    }
}

