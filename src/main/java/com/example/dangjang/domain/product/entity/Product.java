package com.example.dangjang.domain.product.entity;

import com.example.dangjang.common.entity.BaseTimeEntity;
import com.example.dangjang.common.exception.BusinessException;
import com.example.dangjang.common.exception.ErrorCode;
import com.example.dangjang.domain.store.entity.Store;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "products")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "original_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal originalPrice;

    @Column(name = "stock_quantity", nullable = false)
    private Integer stockQuantity;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ProductStatus status;

    @Builder
    public Product(Store store, String name, String description,
                   BigDecimal originalPrice, Integer stockQuantity,
                   String imageUrl, ProductStatus status) {
        validateStockQuantity(stockQuantity);
        this.store = store;
        this.name = name;
        this.description = description;
        this.originalPrice = originalPrice;
        this.stockQuantity = stockQuantity;
        this.imageUrl = imageUrl;
        this.status = status;
    }

    public void update(String name, String description, BigDecimal originalPrice,
                       Integer stockQuantity, String imageUrl, ProductStatus status) {
        validateStockQuantity(stockQuantity);
        this.name = name;
        this.description = description;
        this.originalPrice = originalPrice;
        this.stockQuantity = stockQuantity;
        this.imageUrl = imageUrl;
        this.status = status;
    }

    public void changeStore(Store store) {
        this.store = store;
    }

    public void deactivate() {
        this.status = ProductStatus.INACTIVE;
    }

    private void validateStockQuantity(Integer stockQuantity) {
        if (stockQuantity == null || stockQuantity < 0) {
            throw new BusinessException(ErrorCode.INVALID_STOCK_QUANTITY);
        }
    }
}
