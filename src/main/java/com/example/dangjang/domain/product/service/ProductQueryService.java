package com.example.dangjang.domain.product.service;

import com.example.dangjang.common.exception.BusinessException;
import com.example.dangjang.common.exception.ErrorCode;
import com.example.dangjang.domain.discount.entity.ProductDiscount;
import com.example.dangjang.domain.discount.repository.ProductDiscountRepository;
import com.example.dangjang.domain.product.dto.ProductDetailResponse;
import com.example.dangjang.domain.product.dto.ProductSearchResponse;
import com.example.dangjang.domain.product.dto.ProductSearchSummaryResponse;
import com.example.dangjang.domain.product.entity.Product;
import com.example.dangjang.domain.product.entity.ProductStatus;
import com.example.dangjang.domain.product.repository.ProductRepository;
import com.example.dangjang.domain.recommendation.service.RecommendationScoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ProductQueryService {

    private final ProductRepository productRepository;
    private final ProductDiscountRepository productDiscountRepository;
    private final RecommendationScoreService recommendationScoreService;

    @Transactional(readOnly = true)
    public ProductSearchResponse searchProducts(String keyword, BigDecimal minPrice, BigDecimal maxPrice, int page, int size) {
        if (minPrice != null && maxPrice != null && minPrice.compareTo(maxPrice) > 0) {
            throw new BusinessException(ErrorCode.INVALID_SEARCH_CONDITION);
        }

        PageRequest pageable = PageRequest.of(page, size);
        var resultPage = productRepository.searchByNameAndPriceRange(
                keyword,
                ProductStatus.INACTIVE,
                minPrice,
                maxPrice,
                pageable
        );

        var now = LocalDateTime.now();
        var content = resultPage.getContent().stream()
                .map(product -> toSummary(product, now))
                .toList();

        recommendationScoreService.trackSearchByProductIds(
                resultPage.getContent().stream().map(Product::getId).toList()
        );

        return new ProductSearchResponse(
                content,
                resultPage.getNumber(),
                resultPage.getSize(),
                resultPage.getTotalElements(),
                resultPage.getTotalPages()
        );
    }


    @Transactional(readOnly = true)
    public ProductDetailResponse getProductDetail(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));

        recommendationScoreService.trackProductDetailView(productId);

        LocalDateTime now = LocalDateTime.now();
        ProductDiscount activeDiscount = productDiscountRepository.findByProduct(product).stream()
                .filter(discount -> discount.isActiveNow(now))
                .max((a, b) -> a.getId().compareTo(b.getId()))
                .orElse(null);

        boolean hasActiveDiscount = activeDiscount != null;
        var store = product.getStore();
        var market = store.getMarket();

        return new ProductDetailResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                store.getId(),
                store.getName(),
                market.getId(),
                market.getName(),
                product.getOriginalPrice(),
                product.getStockQuantity(),
                product.getImageUrl(),
                product.getStatus(),
                hasActiveDiscount,
                hasActiveDiscount ? activeDiscount.getDiscountPrice() : null,
                hasActiveDiscount ? toDiscountLabel(activeDiscount) : null
        );
    }

    private ProductSearchSummaryResponse toSummary(Product product, LocalDateTime now) {
        var store = product.getStore();
        var market = store.getMarket();

        ProductDiscount activeDiscount = productDiscountRepository.findByProduct(product).stream()
                .filter(discount -> discount.isActiveNow(now))
                .max((a, b) -> a.getId().compareTo(b.getId()))
                .orElse(null);

        boolean hasActiveDiscount = activeDiscount != null;
        BigDecimal discountPrice = hasActiveDiscount ? activeDiscount.getDiscountPrice() : null;
        String discountLabel = hasActiveDiscount ? toDiscountLabel(activeDiscount) : null;

        return new ProductSearchSummaryResponse(
                product.getId(),
                store.getId(),
                store.getName(),
                market.getId(),
                market.getName(),
                product.getName(),
                product.getOriginalPrice(),
                product.getImageUrl(),
                hasActiveDiscount,
                discountPrice,
                discountLabel
        );
    }

    private String toDiscountLabel(ProductDiscount discount) {
        if (discount.getDiscountType().isPercent()) {
            BigDecimal percent = discount.getDiscountValue().setScale(0, RoundingMode.DOWN);
            return percent.toPlainString() + "% 할인";
        }

        BigDecimal amount = discount.getDiscountValue().setScale(0, RoundingMode.DOWN);
        return amount.toPlainString() + "원 할인";
    }
}

