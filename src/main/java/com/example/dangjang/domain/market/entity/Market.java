package com.example.dangjang.domain.market.entity;

import com.example.dangjang.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "markets")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Market extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "market_id")
    private Long id;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, length = 255)
    private String address;

    @Column(length = 100)
    private String city;

    @Column(length = 100)
    private String district;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MarketStatus status;

    @Builder
    public Market(String name, String description, String address,
                  String city, String district,
                  MarketStatus status) {
        this.name = name;
        this.description = description;
        this.address = address;
        this.city = city;
        this.district = district;
        this.status = status;
    }

    public void update(String name, String description, String address,
                       String city, String district,
                       MarketStatus status) {
        this.name = name;
        this.description = description;
        this.address = address;
        this.city = city;
        this.district = district;
        this.status = status;
    }

    public void deactivate() {
        this.status = MarketStatus.INACTIVE;
    }
}
