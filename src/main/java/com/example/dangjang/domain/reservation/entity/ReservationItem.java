package com.example.dangjang.domain.reservation.entity;

import com.example.dangjang.domain.discount.entity.ProductDiscount;
import com.example.dangjang.domain.product.entity.Product;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "reservation_items")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReservationItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reservation_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "reservation_id", nullable = false)
    private Reservation reservation;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_discount_id")
    private ProductDiscount productDiscount;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "discount_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal discountPrice;

    @Builder
    public ReservationItem(Reservation reservation, Product product, ProductDiscount productDiscount, Integer quantity, BigDecimal discountPrice) {
        this.reservation = reservation;
        this.product = product;
        this.productDiscount = productDiscount;
        this.quantity = quantity;
        this.discountPrice = discountPrice;
    }
}
