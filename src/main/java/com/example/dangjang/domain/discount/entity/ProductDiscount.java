package com.example.dangjang.domain.discount.entity;

import com.example.dangjang.common.entity.BaseTimeEntity;
import com.example.dangjang.common.exception.BusinessException;
import com.example.dangjang.common.exception.ErrorCode;
import com.example.dangjang.domain.product.entity.Product;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "product_discounts")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductDiscount extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_discount_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false, length = 150)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(name = "discount_type", nullable = false, length = 20)
    private DiscountType discountType;

    @Column(name = "discount_value", nullable = false, precision = 10, scale = 2)
    private BigDecimal discountValue;

    @Column(name = "discount_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal discountPrice;

    @Column(name = "start_at", nullable = false)
    private LocalDateTime startAt;

    @Column(name = "end_at", nullable = false)
    private LocalDateTime endAt;

    @Column(name = "remaining_quantity", nullable = false)
    private Integer remainingQuantity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private DiscountStatus status;

    @Builder
    public ProductDiscount(Product product, String title, DiscountType discountType,
                           BigDecimal discountValue, BigDecimal discountPrice,
                           LocalDateTime startAt, LocalDateTime endAt,
                           Integer remainingQuantity, DiscountStatus status) {
        validate(startAt, endAt, discountValue, discountPrice, remainingQuantity);
        this.product = product;
        this.title = title;
        this.discountType = discountType;
        this.discountValue = discountValue;
        this.discountPrice = discountPrice;
        this.startAt = startAt;
        this.endAt = endAt;
        this.remainingQuantity = remainingQuantity;
        this.status = status;
    }

    public void update(String title, DiscountType discountType,
                       BigDecimal discountValue, BigDecimal discountPrice,
                       LocalDateTime startAt, LocalDateTime endAt,
                       Integer remainingQuantity, DiscountStatus status) {
        validate(startAt, endAt, discountValue, discountPrice, remainingQuantity);
        this.title = title;
        this.discountType = discountType;
        this.discountValue = discountValue;
        this.discountPrice = discountPrice;
        this.startAt = startAt;
        this.endAt = endAt;
        this.remainingQuantity = remainingQuantity;
        this.status = status;
    }

    public void changeProduct(Product product) {
        this.product = product;
    }

    public void changeStatus(DiscountStatus status) {
        this.status = status;
    }

    public boolean isActiveNow(LocalDateTime now) {
        return status == DiscountStatus.ACTIVE
                && !now.isBefore(startAt)
                && !now.isAfter(endAt)
                && remainingQuantity > 0;
    }

    public void decreaseRemainingQuantity(int quantity) {
        if (quantity <= 0 || this.remainingQuantity < quantity) {
            throw new BusinessException(ErrorCode.RESERVATION_QUANTITY_EXCEEDED);
        }
        this.remainingQuantity -= quantity;
    }

    private void validate(LocalDateTime startAt, LocalDateTime endAt,
                          BigDecimal discountValue, BigDecimal discountPrice,
                          Integer remainingQuantity) {
        if (startAt == null || endAt == null || !startAt.isBefore(endAt)) {
            throw new BusinessException(ErrorCode.INVALID_DISCOUNT_TIME);
        }

        if (discountValue == null || discountValue.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException(ErrorCode.INVALID_DISCOUNT_VALUE);
        }

        if (discountPrice == null || discountPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException(ErrorCode.INVALID_DISCOUNT_VALUE);
        }

        if (remainingQuantity == null || remainingQuantity < 0) {
            throw new BusinessException(ErrorCode.INVALID_REMAINING_QUANTITY);
        }
    }
}
