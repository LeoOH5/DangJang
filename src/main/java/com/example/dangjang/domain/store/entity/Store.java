package com.example.dangjang.domain.store.entity;

import com.example.dangjang.common.entity.BaseTimeEntity;
import com.example.dangjang.domain.market.entity.Market;
import com.example.dangjang.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Entity
@Table(name = "stores")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Store extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "store_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "market_id", nullable = false)
    private Market market;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 30)
    private String phone;

    @Column(length = 255)
    private String address;

    @Column(name = "open_time")
    private LocalTime openTime;

    @Column(name = "close_time")
    private LocalTime closeTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StoreStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private User owner;

    @Builder
    public Store(Market market, String name, String description, String phone,
                 String address, LocalTime openTime, LocalTime closeTime,
                 StoreStatus status, User owner) {
        this.market = market;
        this.name = name;
        this.description = description;
        this.phone = phone;
        this.address = address;
        this.openTime = openTime;
        this.closeTime = closeTime;
        this.status = status;
        this.owner = owner;
    }

    public void update(String name, String description, String phone,
                       String address, LocalTime openTime, LocalTime closeTime,
                       StoreStatus status) {
        this.name = name;
        this.description = description;
        this.phone = phone;
        this.address = address;
        this.openTime = openTime;
        this.closeTime = closeTime;
        this.status = status;
    }

    public void changeMarket(Market market) {
        this.market = market;
    }

    public void deactivate() {
        this.status = StoreStatus.INACTIVE;
    }
}
