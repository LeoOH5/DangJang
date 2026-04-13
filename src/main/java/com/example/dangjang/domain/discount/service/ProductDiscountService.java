package com.example.dangjang.domain.discount.service;

import com.example.dangjang.common.exception.BusinessException;
import com.example.dangjang.common.exception.ErrorCode;
import com.example.dangjang.domain.discount.dto.ProductDiscountCreateRequest;
import com.example.dangjang.domain.discount.dto.ProductDiscountCreateResponse;
import com.example.dangjang.domain.discount.dto.ProductDiscountUpdateRequest;
import com.example.dangjang.domain.discount.dto.ProductDiscountUpdateResponse;
import com.example.dangjang.domain.discount.dto.ProductDiscountStatusUpdateRequest;
import com.example.dangjang.domain.discount.dto.ProductDiscountStatusUpdateResponse;
import com.example.dangjang.domain.discount.entity.DiscountStatus;
import com.example.dangjang.domain.discount.entity.DiscountType;
import com.example.dangjang.domain.discount.entity.ProductDiscount;
import com.example.dangjang.domain.discount.repository.ProductDiscountRepository;
import com.example.dangjang.domain.product.entity.Product;
import com.example.dangjang.domain.product.entity.ProductStatus;
import com.example.dangjang.domain.notification.service.NotificationDispatchService;
import com.example.dangjang.domain.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductDiscountService {

    private final ProductDiscountRepository productDiscountRepository;
    private final ProductRepository productRepository;
    private final NotificationDispatchService notificationDispatchService;

    @Transactional
    public ProductDiscountCreateResponse createProductDiscount(ProductDiscountCreateRequest request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));

        if (product.getStatus() == ProductStatus.INACTIVE) {
            throw new BusinessException(ErrorCode.PRODUCT_INACTIVE);
        }

        LocalDateTime startAt = LocalDateTime.parse(request.getStartAt());
        LocalDateTime endAt = LocalDateTime.parse(request.getEndAt());

        if (!startAt.isBefore(endAt)) {
            throw new BusinessException(ErrorCode.INVALID_DISCOUNT_TIME);
        }

        BigDecimal discountPrice = calculateDiscountPrice(
                product.getOriginalPrice(),
                request.getDiscountType(),
                request.getDiscountValue()
        );

        DiscountStatus status = startAt.isAfter(LocalDateTime.now())
                ? DiscountStatus.SCHEDULED
                : DiscountStatus.ACTIVE;

        validateDiscountConflicts(product, request, startAt, endAt);

        ProductDiscount saved = productDiscountRepository.save(ProductDiscount.builder()
                .product(product)
                .title(request.getTitle())
                .discountType(request.getDiscountType())
                .discountValue(request.getDiscountValue())
                .discountPrice(discountPrice)
                .startAt(startAt)
                .endAt(endAt)
                .remainingQuantity(request.getRemainingQuantity())
                .status(status)
                .build());

        notificationDispatchService.notifyDiscountCreatedForStoreFavorites(
                product.getStore(),
                saved.getId(),
                saved.getTitle()
        );

        return new ProductDiscountCreateResponse(saved.getId(), saved.getDiscountPrice(), saved.getStatus());
    }

    @Transactional
    public ProductDiscountUpdateResponse updateProductDiscount(Long productDiscountId, ProductDiscountUpdateRequest request) {
        ProductDiscount discount = productDiscountRepository.findById(productDiscountId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_DISCOUNT_NOT_FOUND));

        Product product = discount.getProduct();

        if (product.getStatus() == ProductStatus.INACTIVE) {
            throw new BusinessException(ErrorCode.PRODUCT_INACTIVE);
        }

        LocalDateTime startAt = LocalDateTime.parse(request.getStartAt());
        LocalDateTime endAt = LocalDateTime.parse(request.getEndAt());

        if (!startAt.isBefore(endAt)) {
            throw new BusinessException(ErrorCode.INVALID_DISCOUNT_TIME);
        }

        BigDecimal discountPrice = calculateDiscountPrice(
                product.getOriginalPrice(),
                request.getDiscountType(),
                request.getDiscountValue()
        );

        LocalDateTime now = LocalDateTime.now();
        DiscountStatus status = now.isBefore(startAt)
                ? DiscountStatus.SCHEDULED
                : (now.isAfter(endAt) ? DiscountStatus.ENDED : DiscountStatus.ACTIVE);

        validateDiscountConflictsForUpdate(product, productDiscountId, request, startAt, endAt);

        discount.update(
                request.getTitle(),
                request.getDiscountType(),
                request.getDiscountValue(),
                discountPrice,
                startAt,
                endAt,
                request.getRemainingQuantity(),
                status
        );

        return new ProductDiscountUpdateResponse(discount.getId(), discountPrice, status);
    }

    @Transactional
    public ProductDiscountStatusUpdateResponse updateProductDiscountStatus(Long productDiscountId,
                                                                                  ProductDiscountStatusUpdateRequest request) {
        ProductDiscount discount = productDiscountRepository.findById(productDiscountId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_DISCOUNT_NOT_FOUND));

        discount.changeStatus(request.getStatus());
        return new ProductDiscountStatusUpdateResponse(discount.getId(), discount.getStatus());
    }

    private BigDecimal calculateDiscountPrice(BigDecimal originalPrice, DiscountType type, BigDecimal value) {
        if (originalPrice == null || originalPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException(ErrorCode.INVALID_DISCOUNT_VALUE);
        }
        if (value == null || value.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException(ErrorCode.INVALID_DISCOUNT_VALUE);
        }

        if (type.isPercent()) {
            if (value.compareTo(new BigDecimal("100")) > 0) {
                throw new BusinessException(ErrorCode.INVALID_DISCOUNT_VALUE);
            }

            BigDecimal multiplier = BigDecimal.ONE.subtract(value.divide(new BigDecimal("100"), 6, RoundingMode.HALF_UP));
            BigDecimal price = originalPrice.multiply(multiplier).setScale(2, RoundingMode.HALF_UP);
            if (price.compareTo(BigDecimal.ZERO) < 0) {
                throw new BusinessException(ErrorCode.INVALID_DISCOUNT_VALUE);
            }
            return price;
        }

        if (type.isAmount()) {
            BigDecimal price = originalPrice.subtract(value).setScale(2, RoundingMode.HALF_UP);
            if (price.compareTo(BigDecimal.ZERO) < 0) {
                throw new BusinessException(ErrorCode.INVALID_DISCOUNT_VALUE);
            }
            return price;
        }

        throw new BusinessException(ErrorCode.INVALID_DISCOUNT_VALUE);
    }

    private void validateDiscountConflicts(
            Product product,
            ProductDiscountCreateRequest request,
            LocalDateTime startAt,
            LocalDateTime endAt
    ) {
        LocalDateTime now = LocalDateTime.now();
        List<ProductDiscount> discounts = productDiscountRepository.findByProduct(product);

        boolean existsSame = discounts.stream().anyMatch(d ->
                d.getTitle().equals(request.getTitle())
                        && d.getDiscountType().normalize() == request.getDiscountType().normalize()
                        && d.getDiscountValue().compareTo(request.getDiscountValue()) == 0
                        && d.getStartAt().isEqual(startAt)
                        && d.getEndAt().isEqual(endAt)
        );
        if (existsSame) {
            throw new BusinessException(ErrorCode.DISCOUNT_ALREADY_EXISTS);
        }

        boolean activeConflict = discounts.stream().anyMatch(d -> d.isActiveNow(now));
        if (activeConflict) {
            throw new BusinessException(ErrorCode.ACTIVE_DISCOUNT_CONFLICT);
        }

        boolean overlapConflict = discounts.stream()
                .filter(d -> d.getStatus() == DiscountStatus.SCHEDULED || d.getStatus() == DiscountStatus.ACTIVE)
                .anyMatch(d -> startAt.isBefore(d.getEndAt()) && endAt.isAfter(d.getStartAt()));
        if (overlapConflict) {
            throw new BusinessException(ErrorCode.ACTIVE_DISCOUNT_CONFLICT);
        }
    }

    private void validateDiscountConflictsForUpdate(
            Product product,
            Long productDiscountId,
            ProductDiscountUpdateRequest request,
            LocalDateTime startAt,
            LocalDateTime endAt
    ) {
        LocalDateTime now = LocalDateTime.now();
        List<ProductDiscount> discounts = productDiscountRepository.findByProduct(product);

        boolean existsSame = discounts.stream().anyMatch(d ->
                !d.getId().equals(productDiscountId)
                        && d.getTitle().equals(request.getTitle())
                        && d.getDiscountType().normalize() == request.getDiscountType().normalize()
                        && d.getDiscountValue().compareTo(request.getDiscountValue()) == 0
                        && d.getStartAt().isEqual(startAt)
                        && d.getEndAt().isEqual(endAt)
        );
        if (existsSame) {
            throw new BusinessException(ErrorCode.DISCOUNT_ALREADY_EXISTS);
        }

        boolean activeConflict = discounts.stream().anyMatch(d ->
                !d.getId().equals(productDiscountId)
                        && d.isActiveNow(now)
        );
        if (activeConflict) {
            throw new BusinessException(ErrorCode.ACTIVE_DISCOUNT_CONFLICT);
        }

        boolean overlapConflict = discounts.stream()
                .filter(d -> !d.getId().equals(productDiscountId))
                .filter(d -> d.getStatus() == DiscountStatus.SCHEDULED || d.getStatus() == DiscountStatus.ACTIVE)
                .anyMatch(d -> startAt.isBefore(d.getEndAt()) && endAt.isAfter(d.getStartAt()));
        if (overlapConflict) {
            throw new BusinessException(ErrorCode.ACTIVE_DISCOUNT_CONFLICT);
        }
    }
}

