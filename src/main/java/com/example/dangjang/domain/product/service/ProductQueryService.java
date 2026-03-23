package com.example.dangjang.domain.product.service;

import com.example.dangjang.domain.discount.entity.ProductDiscount;
import com.example.dangjang.domain.discount.repository.ProductDiscountRepository;
import com.example.dangjang.domain.product.dto.ProductSearchResponse;
import com.example.dangjang.domain.product.dto.ProductSearchSummaryResponse;
import com.example.dangjang.domain.product.entity.Product;
import com.example.dangjang.domain.product.entity.ProductStatus;
import com.example.dangjang.domain.product.repository.ProductRepository;
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

    @Transactional(readOnly = true)
    public ProductSearchResponse searchProducts(String keyword, int page, int size) {
        PageRequest pageable = PageRequest.of(page, size);
        var resultPage = productRepository.findByNameContainingIgnoreCaseAndStatusNot(
                keyword,
                ProductStatus.INACTIVE,
                pageable
        );

        var now = LocalDateTime.now();
        var content = resultPage.getContent().stream()
                .map(product -> toSummary(product, now))
                .toList();

        return new ProductSearchResponse(
                content,
                resultPage.getNumber(),
                resultPage.getSize(),
                resultPage.getTotalElements(),
                resultPage.getTotalPages()
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

